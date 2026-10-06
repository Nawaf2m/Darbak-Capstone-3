package com.example.tuwaiqcapstone3.Service;

import org.springframework.web.util.HtmlUtils;

// HTML email templates (inline CSS + tables, because email apps ignore most modern CSS)
public class EmailTemplates {

    public static String welcome(String name) {
        String safeName = HtmlUtils.htmlEscape(name);

        return """
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1"></head>
<body style="margin:0;padding:0;background-color:#F1F3F2;font-family:Arial,Tahoma,sans-serif;">
<table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="background-color:#F1F3F2;">
<tr><td align="center" style="padding:24px 12px;">

  <table role="presentation" width="600" cellpadding="0" cellspacing="0" style="width:100%;max-width:600px;">

    <!-- HEADER / HERO -->
    <tr><td style="background-color:#12332A;border-radius:16px 16px 0 0;padding:28px 32px;">
      <table role="presentation" cellpadding="0" cellspacing="0"><tr>
        <td style="background-color:#2F7D5C;border-radius:8px;width:34px;height:34px;text-align:center;font-size:18px;line-height:34px;">&#128663;</td>
        <td style="padding-left:10px;color:#FFFFFF;font-size:16px;font-weight:bold;">Darbak</td>
      </tr></table>

      <p style="margin:26px 0 10px 0;">
        <span style="display:inline-block;border:1px solid #6FA58F;border-radius:20px;padding:4px 10px;color:#CFE8DC;font-size:10px;letter-spacing:1px;">GO TOGETHER. CHEER TOGETHER.</span>
      </p>
      <h1 style="margin:0 0 8px 0;color:#FFFFFF;font-size:28px;line-height:34px;">Welcome aboard, {{name}}!</h1>
      <p style="margin:0;color:#B7D3C7;font-size:14px;line-height:20px;">Less parking. More football. Find fans heading your way.</p>
    </td></tr>

    <!-- BODY -->
    <tr><td style="background-color:#FFFFFF;padding:28px 32px;">

      <p style="margin:0 0 18px 0;color:#1B1B1B;font-size:15px;line-height:22px;">
        Your account has been created successfully. Here is how to get started:
      </p>

      <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border:1px solid #E3E8E5;border-radius:10px;margin-bottom:12px;">
        <tr><td style="padding:14px 16px;">
          <div style="color:#1B1B1B;font-size:14px;font-weight:bold;">Find your matchday crew</div>
          <div style="color:#6B7280;font-size:13px;line-height:19px;margin-top:4px;">Pick a match and join a ride with fellow fans.</div>
        </td></tr>
      </table>

      <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border:1px solid #E3E8E5;border-radius:10px;">
        <tr><td style="padding:14px 16px;">
          <div style="color:#1B1B1B;font-size:14px;font-weight:bold;">Have room for a few more?</div>
          <div style="color:#6B7280;font-size:13px;line-height:19px;margin-top:4px;">Become a driver and share the journey with other fans.</div>
        </td></tr>
      </table>

      <!-- ARABIC -->
      <hr style="border:none;border-top:1px solid #E3E8E5;margin:28px 0;">

      <div dir="rtl" style="text-align:right;">
        <h2 style="margin:0 0 8px 0;color:#12332A;font-size:22px;">أهلًا بك، {{name}}!</h2>
        <p style="margin:0 0 18px 0;color:#1B1B1B;font-size:15px;line-height:24px;">
          تم إنشاء حسابك بنجاح. مواصلات أقل وكرة قدم أكثر، وهذه طريقة البدء:
        </p>

        <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border:1px solid #E3E8E5;border-radius:10px;margin-bottom:12px;">
          <tr><td style="padding:14px 16px;text-align:right;">
            <div style="color:#1B1B1B;font-size:14px;font-weight:bold;">اعثر على رفقاء يوم المباراة</div>
            <div style="color:#6B7280;font-size:13px;line-height:21px;margin-top:4px;">اختر مباراة وانضم إلى رحلة مع مشجعين آخرين.</div>
          </td></tr>
        </table>

        <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="border:1px solid #E3E8E5;border-radius:10px;">
          <tr><td style="padding:14px 16px;text-align:right;">
            <div style="color:#1B1B1B;font-size:14px;font-weight:bold;">عندك مقاعد فاضية؟</div>
            <div style="color:#6B7280;font-size:13px;line-height:21px;margin-top:4px;">كن سائقًا وشارك الرحلة مع المشجعين.</div>
          </td></tr>
        </table>
      </div>
    </td></tr>

    <!-- FOOTER -->
    <tr><td style="background-color:#FFFFFF;border-top:1px solid #E3E8E5;border-radius:0 0 16px 16px;padding:16px 32px;text-align:center;color:#9CA3AF;font-size:11px;line-height:17px;">
      This is an automated message, please do not reply.<br>
      هذه رسالة تلقائية، يرجى عدم الرد عليها.
    </td></tr>

  </table>

</td></tr>
</table>
</body>
</html>
""".replace("{{name}}", safeName);
    }
}