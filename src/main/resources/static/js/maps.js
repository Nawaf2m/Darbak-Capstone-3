"use strict";
const Maps = {
  pending:null,
  async load() {
    if(window.google?.maps?.Map) return window.google.maps;
    if(this.pending) return this.pending;
    const key=localStorage.getItem("darbak.mapsKey");
    if(!key) throw new Error("Add your Google Maps browser key in Map settings, or use your device location.");
    this.pending=new Promise((resolve,reject)=>{
      let timeout;
      const fail=()=>{clearTimeout(timeout);this.pending=null;reject(new Error("Google Maps could not load. Check the browser key and enabled Maps JavaScript API."));};
      window.darbakMapReady=()=>{clearTimeout(timeout);resolve(window.google.maps);};
      window.gm_authFailure=fail;
      const script=document.createElement("script");
      const params=new URLSearchParams({key,loading:"async",callback:"darbakMapReady",v:"weekly",libraries:"marker",language:"en",region:"SA"});
      script.src="https://maps.googleapis.com/maps/api/js?"+params;
      script.async=true;script.onerror=fail;
      timeout=setTimeout(fail,20000);
      document.head.append(script);
    });
    return this.pending;
  },
  async show(container,point,onSelect,destination) {
    const maps=await this.load();
    if(!container.isConnected) return;
    const center={lat:Number(point.lat),lng:Number(point.lng)};
    container.replaceChildren();
    const map=new maps.Map(container,{center,zoom:12,mapId:"DEMO_MAP_ID",streetViewControl:false,mapTypeControl:false,fullscreenControl:true});
    const {AdvancedMarkerElement}=await maps.importLibrary("marker");
    const marker=new AdvancedMarkerElement({map,position:center,title:onSelect?"Meeting point":"Meeting point"});
    if(destination) {
      const end={lat:Number(destination.lat),lng:Number(destination.lng)};
      new AdvancedMarkerElement({map,position:end,title:"Stadium"});
      const bounds=new maps.LatLngBounds();bounds.extend(center);bounds.extend(end);map.fitBounds(bounds,55);
    }
    if(onSelect) map.addListener("click",event=>{
      if(!event.latLng) return;
      marker.position=event.latLng;onSelect(event.latLng.lat(),event.latLng.lng());
    });
    return map;
  },
  locate() {
    return new Promise((resolve,reject)=>{
      if(!navigator.geolocation) return reject(new Error("Device location is not supported in this browser."));
      navigator.geolocation.getCurrentPosition(position=>resolve({lat:position.coords.latitude,lng:position.coords.longitude}),
        error=>reject(new Error(error.code===1?"Location permission was denied. Choose a point on the map or enter coordinates.":"Could not get your location. Choose a point on the map or enter coordinates.")),
        {enableHighAccuracy:true,timeout:15000,maximumAge:60000});
    });
  }
};
