# bilichat
b站直播弹幕显式。用java实现是因为“又不是不能用”。
没有区分直播间ID和实际直播间URL对应的地址，实际上这两个可以是不一样的(比如大主播可以用比较好记的直播间URL)。不过我一样，所以这样写”又不是不能用“。

## how get live stream 弹幕
1. get websockets url by `https://api.live.bilibili.com/xlive/web-room/v1/index/getDanmuInfo`
2. connect
3. heartbeat response from serve will return 人气值

## reference
* [github Bilibili API 直播，番剧文档](https://github.com/lovelyyoshino/Bilibili-Live-API)
* [github 哔哩哔哩-API收集整理](https://github.com/SocialSisterYi/bilibili-API-collect)

## todo list
1. change content color to red when received a new message, then turn to white gradually.

## questions
1. lock when using singleton 
2. spi
3. copyOnWriteArrayList

