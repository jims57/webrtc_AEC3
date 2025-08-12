cmake_minimum_required(VERSION 3.18.1)
project(webrtc_aec3_real)

# ace-key-points.txt: Android优化编译选项
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC -O3 -DANDROID -std=c++17 -Wall")
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fPIC -O3 -DANDROID")

# 只包含当前项目目录
include_directories(${PROJECT_SOURCE_DIR})

# 只编译JNI包装器 - 独立实现AEC3算法，无外部依赖
set(JNI_SOURCES jni/webrtc_aec3_real_jni.cpp)

# 创建共享库
add_library(webrtc_aec3_real SHARED ${JNI_SOURCES})

# 链接Android系统库
find_library(log-lib log)
find_library(m-lib m)
target_link_libraries(webrtc_aec3_real ${log-lib} ${m-lib})
