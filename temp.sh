curl 'https://api.live.bilibili.com/room/v1/Room/startLive' \
  -H 'authority: api.live.bilibili.com' \
  -H 'sec-ch-ua: " Not A;Brand";v="99", "Chromium";v="90", "Google Chrome";v="90"' \
  -H 'accept: application/json, text/plain, */*' \
  -H 'sec-ch-ua-mobile: ?0' \
  -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36' \
  -H 'content-type: application/x-www-form-urlencoded; charset=UTF-8' \
  -H 'origin: https://link.bilibili.com' \
  -H 'sec-fetch-site: same-site' \
  -H 'sec-fetch-mode: cors' \
  -H 'sec-fetch-dest: empty' \
  -H 'referer: https://link.bilibili.com/p/center/index' \
  -H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8,de;q=0.7,en-US;q=0.6' \
  -H $'cookie: _uuid=A9F1D102-EB91-2B93-2680-DBEBA229DC4633338infoc; CURRENT_FNVAL=80; bsource=search_google; blackside_state=1; rpdid=|(u)~m|)YkkY0J\'uY|~RmJY|R; fingerprint3=e9ed7e1ccb723d67b59c349183f6a160; fingerprint_s=e84ab339162c5e14a5812752e85d2ac0; Hm_lvt_8a6e55dbd2870f0f5bc9194cddf32a02=1619433942,1619600077,1619674224; DedeUserID=247897641; DedeUserID__ckMd5=5d27677fd7d53dc6; bp_video_offset_247897641=520916697634182315; bp_t_offset_247897641=520916697634182315; buvid3=2916833D-F74A-4648-8D2C-F7B8EDDFB0B534768infoc; Hm_lpvt_8a6e55dbd2870f0f5bc9194cddf32a02=1620182787; fingerprint=9b6cc1d9c4a1744bac4dd3d124d6c01e; buvid_fp=2916833D-F74A-4648-8D2C-F7B8EDDFB0B534768infoc; buvid_fp_plain=2916833D-F74A-4648-8D2C-F7B8EDDFB0B534768infoc; SESSDATA=810719da%2C1635750898%2C9031d%2A51; bili_jct=4e4306b00650ed0710088cacb083e099; sid=8q0w859r; LIVE_BUVID=AUTO2716202022782981; _dfcaptcha=eec41e2d7972dc61d61ad33125d77524; PVID=1' \
  --data-raw 'room_id=9325157&platform=pc&area_v2=372&csrf_token=4e4306b00650ed0710088cacb083e099&csrf=4e4306b00650ed0710088cacb083e099' \
  --compressed