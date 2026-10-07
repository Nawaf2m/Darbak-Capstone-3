"use strict";
const Session = {
  get() { try { return JSON.parse(localStorage.getItem("darbak.user") || "null"); } catch { return null; } },
  set(user) { localStorage.setItem("darbak.user", JSON.stringify({id:user.id,name:user.name,email:user.email,phoneNumber:user.phoneNumber})); },
  clear() { localStorage.removeItem("darbak.user"); }
};
const BrowserLinks = {
  get() { try { return JSON.parse(localStorage.getItem("darbak.links") || '{"cars":{},"rideCars":{}}'); } catch { return {cars:{},rideCars:{}}; } },
  car(id, userId) { const links=this.get(); links.cars[id]=userId; localStorage.setItem("darbak.links",JSON.stringify(links)); },
  ride(id, carId) { const links=this.get(); links.rideCars[id]=carId; localStorage.setItem("darbak.links",JSON.stringify(links)); }
};
const Api = {
  async request(path, method="GET", body) {
    const controller = new AbortController();
    const timeout = setTimeout(()=>controller.abort(), /ai-|attendance-check|review-summary|\/review\/|football/.test(path)?90000:25000);
    try {
      const response = await fetch("/api/v1"+path, {
        method, headers:body===undefined?{}:{"Content-Type":"application/json"},
        body:body===undefined?undefined:JSON.stringify(body), signal:controller.signal
      });
      const text = await response.text();
      let data; try { data=text?JSON.parse(text):null; } catch { data=text; }
      if (!response.ok) {
        const message = typeof data==="object" && data ? data.message : typeof data==="string" && !data.startsWith("<") ? data : null;
        const error = new Error(message || "Something went wrong. Please try again.");
        error.status=response.status; throw error;
      }
      return data;
    } catch(error) {
      if(error.name==="AbortError") throw new Error("This is taking longer than expected. Please try again.");
      if(error instanceof TypeError) throw new Error("Cannot reach Darbak. Make sure the backend is running.");
      throw error;
    } finally { clearTimeout(timeout); }
  },
  async list(path) {
    try { const data=await this.request(path); if(!Array.isArray(data)) throw new Error("The server did not return a list."); return data; }
    catch(error) {
      if(error.status===400 && /^(there is no|no .*found|no available|you dont have requests|ride dont have participant|there is no participant)/i.test(error.message)) return [];
      throw error;
    }
  },
  async login(email,password) {
    await this.request("/user/login/"+encodeURIComponent(email)+"/"+encodeURIComponent(password),"POST");
    const users=await this.list("/user/get");
    const user=users.find(user=>user.email.toLowerCase()===email.toLowerCase());
    if(!user) throw new Error("Your account could not be loaded. Please sign in again.");
    Session.set(user); return Session.get();
  },
  safeUser(user) { return {id:user.id,name:user.name,email:user.email,phoneNumber:user.phoneNumber,banned:user.banned,createdAt:user.createdAt}; },
  async catalog() {
    const [matches,rides,cars,rawUsers,stadiums] = await Promise.all([
      this.list("/match/get"),this.list("/Ride/get"),this.list("/car/get"),this.list("/user/get"),this.list("/stadium/get")
    ]);
    const users=rawUsers.map(user=>this.safeUser(user));
    const warnings=[];
    // Current entities hide their relationships; existing read endpoints supply those links.
    const maps={};
    await Promise.all(rides.map(async ride=>{
      try { maps[ride.id]=await this.request("/Ride/map/"+ride.id); }
      catch(error) { warnings.push("Location details for ride #"+ride.id+" are unavailable."); }
    }));
    const matchIds={};
    if(rides.some(ride=>!maps[ride.id])) {
      await Promise.all(matches.map(async match=>{
        try { (await this.list("/Ride/match/"+match.id)).forEach(ride=>matchIds[ride.id]=match.id); }
        catch(error) { warnings.push("Some match details could not be loaded."); }
      }));
    }
    const driverIds={}; const requestUsers={};
    await Promise.all(users.map(async user=>{
      const results=await Promise.allSettled([this.list("/Ride/driver/"+user.id),this.list("/RideRequest/user/"+user.id)]);
      if(results[0].status==="fulfilled") results[0].value.forEach(ride=>driverIds[ride.id]=user.id);
      else warnings.push("Some driver details could not be loaded.");
      if(results[1].status==="fulfilled") results[1].value.forEach(request=>requestUsers[request.id]=user.id);
      else warnings.push("Some passenger details could not be loaded.");
    }));
    const links=BrowserLinks.get(); const requestRows=[];
    for(const ride of rides) {
      ride.driverId=driverIds[ride.id] ?? ride.driver?.id;
      ride.driver=users.find(user=>user.id===ride.driverId);
      ride.map=maps[ride.id];
      ride.matchId=ride.map?.matchId ?? matchIds[ride.id] ?? ride.match?.id;
      ride.match=matches.find(match=>match.id===ride.matchId) || ride.match;
      ride.carId=links.rideCars[ride.id];
      ride.car=cars.find(car=>car.id===ride.carId);
      for(const request of ride.rideRequests || []) {
        requestRows.push({...request,rideId:ride.id,passengerId:requestUsers[request.id],ride,passenger:users.find(user=>user.id===requestUsers[request.id])});
      }
    }
    return {matches,rides,cars,users,stadiums,requests:requestRows,maps,warnings:[...new Set(warnings)]};
  },
  async passengers(rideId,users) {
    const rows=await this.list("/RidePerticipant/ride/"+rideId);
    if(!rows.length) return [];
    const people={};
    const results=await Promise.allSettled(users.map(async user=>{
      try { const row=await this.request("/RidePerticipant/user/"+user.id+"/ride/"+rideId); people[row.id]=user; }
      catch(error) { if(error.status===400 && /ride participant not found/.test(error.message)) return; throw error; }
    }));
    if(results.some(result=>result.status==="rejected")) throw new Error("Some passenger details could not be loaded. Please retry.");
    return rows.map(row=>({...row,user:people[row.id],userId:people[row.id]?.id,rideId}));
  }
};
