# WebRTC AEC3 Real - Android集成指南

## 概述
这是基于真正WebRTC AEC3算法的Android AAR库，专为TTS回声消除设计。

## 关键特性
- ✅ **真正的WebRTC AEC3算法** - 与Google Meet等产品使用相同技术
- ✅ **48kHz采样率支持** - 符合ace-key-points.txt要求
- ✅ **10ms块处理** - 480样本@48kHz的实时处理
- ✅ **移动设备优化** - Android平台专门优化
- ✅ **TTS专用设计** - 针对Text-to-Speech场景优化

## 快速集成

### 1. 添加AAR依赖
```gradle
// app/build.gradle
android {
    ...
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.aar'])
}
```

### 2. 基本使用
```java
// 创建AEC3处理器
WebRTCAEC3Real aec3 = new WebRTCAEC3Real();
boolean success = aec3.create(48000, 1, true); // 48kHz, 单声道, 移动优化

// 设置TTS延迟补偿 (Android典型值: 80-150ms)
aec3.setStreamDelay(100);

// 处理TTS音频 (在播放前5-20ms调用)
float[] ttsData = new float[480]; // 10ms @ 48kHz
aec3.analyzeRender(ttsData);

// 处理麦克风音频 (移除TTS回声)
float[] micData = new float[480];
float[] cleanAudio = new float[480];
aec3.processCapture(micData, cleanAudio, false);

// 获取性能指标
float erle = aec3.getERLE(); // >15dB表示良好的回声消除
int delay = aec3.getDetectedDelay();

// 清理
aec3.destroy();
```

### 3. 完整TTS流程
```java
public class TTSAECManager {
    private WebRTCAEC3Real aec3;
    private AudioTrack audioTrack;
    private AudioRecord audioRecord;
    
    public void initializeAEC() {
        aec3 = new WebRTCAEC3Real();
        aec3.create(48000, 1, true);
        aec3.setStreamDelay(100); // Android延迟补偿
    }
    
    public void playTTSWithAEC(float[] ttsAudio) {
        // 1. 先送入AEC进行分析
        aec3.analyzeRender(ttsAudio);
        
        // 2. 然后播放TTS音频
        audioTrack.write(ttsAudio, 0, ttsAudio.length, AudioTrack.WRITE_BLOCKING);
    }
    
    public float[] processCleanAudio(float[] micAudio) {
        float[] cleanAudio = new float[480];
        aec3.processCapture(micAudio, cleanAudio, false);
        return cleanAudio; // 无回声的干净音频
    }
}
```

## 性能调优

### 延迟调整
```java
// 根据设备特性调整延迟
// 低端设备: 80-120ms
// 高端设备: 100-150ms
aec3.setStreamDelay(120);
```

### 监控AEC效果
```java
float erle = aec3.getERLE();
if (erle < 10.0f) {
    // 回声消除效果不佳，可能需要调整延迟
    aec3.setStreamDelay(aec3.getDetectedDelay());
}
```

## 注意事项

1. **采样率限制**: 必须使用48kHz，其他采样率不支持
2. **帧大小**: 必须是480样本 (10ms @ 48kHz)
3. **处理顺序**: 先调用analyzeRender，再调用processCapture
4. **延迟补偿**: Android建议使用80-150ms延迟补偿
5. **权限要求**: 需要RECORD_AUDIO权限

## 故障排除

**Q: 没有回声消除效果**
A: 检查延迟设置，尝试调整setStreamDelay值

**Q: 音频质量下降**
A: 确保帧大小正确，检查ERLE值

**Q: 性能问题**
A: 确保启用了移动设备优化模式

更多技术支持请参考ace-key-points.txt文档。
