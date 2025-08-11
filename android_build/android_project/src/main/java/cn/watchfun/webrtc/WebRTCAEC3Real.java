package cn.watchfun.webrtc;

/**
 * 真正的WebRTC AEC3 Android包装类
 * Author: Jimmy Gan
 * Date: 2025-01-28
 * 
 * 专为TTS回声消除设计，符合ace-key-points.txt要求:
 * - 48kHz采样率 (强制要求)
 * - 10ms音频块处理 (480样本 @ 48kHz)
 * - 移动设备优化
 * - TTS延迟补偿 (Android: 80-150ms)
 */
public class WebRTCAEC3Real {
    
    // ace-key-points.txt: 48kHz采样率是强制性的
    public static final int REQUIRED_SAMPLE_RATE = 48000;
    public static final int REQUIRED_FRAME_SIZE = 480;  // 10ms @ 48kHz
    public static final int DEFAULT_STREAM_DELAY = 100; // Android典型延迟
    
    static {
        System.loadLibrary("webrtc_aec3_real");
    }
    
    private long nativePtr = 0;
    
    /**
     * 创建AEC3处理器 - TTS专用配置
     * @param sampleRate 采样率 (必须48000Hz)
     * @param channels 声道数 (通常为1)
     * @param enableMobileMode 启用移动设备优化 (Android推荐true)
     * @return 是否成功创建
     */
    public native boolean create(int sampleRate, int channels, boolean enableMobileMode);
    
    /**
     * 设置流延迟 - TTS回声补偿关键参数
     * ace-key-points.txt: Android典型值80-150ms
     * @param delayMs 延迟毫秒数
     */
    public native void setStreamDelay(int delayMs);
    
    /**
     * 分析TTS参考信号 - 在播放前5-20ms调用
     * ace-key-points.txt: TTS音频必须在播放前送入AEC
     * @param ttsData TTS音频数据 (480个float样本)
     * @return 是否处理成功
     */
    public native boolean analyzeRender(float[] ttsData);
    
    /**
     * 处理麦克风捕获信号 - 移除TTS回声
     * ace-key-points.txt: 先处理TTS→后处理麦克风
     * @param micData 麦克风音频数据 (480个float样本)
     * @param output 处理后的无回声音频 (480个float样本)
     * @param levelChange 音量是否发生变化
     * @return 是否处理成功
     */
    public native boolean processCapture(float[] micData, float[] output, boolean levelChange);
    
    /**
     * 获取AEC性能指标
     * @return ERLE值 (dB) - 衡量回声消除效果，>15dB为良好
     */
    public native float getERLE();
    
    /**
     * 获取检测到的延迟
     * @return 延迟毫秒数
     */
    public native int getDetectedDelay();
    
    /**
     * 重置AEC状态 - 清除缓冲区
     */
    public native void reset();
    
    /**
     * 销毁AEC处理器
     */
    public native void destroy();
    
    /**
     * 验证采样率是否符合要求
     */
    public static boolean isValidSampleRate(int sampleRate) {
        return sampleRate == REQUIRED_SAMPLE_RATE;
    }
    
    /**
     * 验证帧大小是否符合要求
     */
    public static boolean isValidFrameSize(int frameSize) {
        return frameSize == REQUIRED_FRAME_SIZE;
    }
}
