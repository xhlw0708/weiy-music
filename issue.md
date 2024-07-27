- [ ] 我的" 头像（图片）设置不了圆角

- [x] "我的" 头像位置右侧不显示menu图案

- [ ] "我的"TextView跑马灯不起效果

- [ ] "乐馆"歌曲、tablayout滑动事件会被父容器消费

- [ ] "喜欢"调整上方箭头和喜欢TextView位置

- [ ] 播放界面，反复点击播放按钮，退出到主页面，再次进入播放页面会出现不能绑定服务的异常

- [ ] 总结一篇笔记

- [ ] 添加音乐到列表时，添加不成功原因：kt方法定义为

```kotlin
// 调用service中的addSong()方法，这样写只是赋值，并不能直接调用方法
fun callAddSong() = addSong()
// 改正
fun callAddSong(){
    addSong()
}
```

- [ ] 进入主页面，先点击播放栏会启动bindService，播放音乐，然后回到主页面，点击songList中的歌曲，会闪退报java.lang.RuntimeException: Unable to bind to service com.liaowei.music.service.MusicService@a13c77c with Intent { cmp=com.liaowei.music/.service.MusicService (has extras) }: java.lang.IllegalStateException

- [x] 播放页播放到最后一首歌的时候，按钮变为灰色不可点击，这时候，点击不了
  
  <mark>解决</mark>：**调整下一首播放顺序到第一步，再判断index状态的时候**，如果处于倒数第二首歌，应先播放下一首，让index++，index表示当前在播放的歌曲；这个时候图案已经是灰色的，再点击下一首因为会判断index，这个时候index始终是size-1，那么会提示已经是第一首了而不再采取任何措施

- [ ] 当点击songList的歌曲时，bindService会开始播放歌曲，同时会开一个线程用handler去处理歌曲的进度条状态，但是这个时候，播放页并没有打开，导致页面没有创建，定义在fragment中的handler获取进度条的时候就会报错；handler中使用到了binding；
  
  java.lang.IllegalStateException: onGetLayoutInflater() cannot be executed until the Fragment is attached to the FragmentManager.
  
  在老师的帮助下发现原因：service中是new的fragment中的handler，不会走生命周期，使用messenger进行通信，发送fragment的handler

- [ ] 
