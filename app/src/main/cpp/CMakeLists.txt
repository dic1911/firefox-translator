cmake_minimum_required(VERSION 3.22.1)
project("tarkka_rust")

set(TARKKA_ROOT_DIR "/home/david/git/tarkka")
set(RUST_TARGET_DIR "${TARKKA_ROOT_DIR}/target")

if(CMAKE_ANDROID_ARCH_ABI STREQUAL "x86_64")
    set(RUST_TARGET "x86_64-linux-android")
    set(RUST_TARGET_UPPER "X86_64_LINUX_ANDROID")
    set(ANDROID_CLANG_PREFIX "x86_64-linux-android")
elseif(CMAKE_ANDROID_ARCH_ABI STREQUAL "x86")
    set(RUST_TARGET "i686-linux-android")
    set(RUST_TARGET_UPPER "I686_LINUX_ANDROID")
    set(ANDROID_CLANG_PREFIX "i686-linux-android")
elseif(CMAKE_ANDROID_ARCH_ABI STREQUAL "arm64-v8a")
    set(RUST_TARGET "aarch64-linux-android")
    set(RUST_TARGET_UPPER "AARCH64_LINUX_ANDROID")
    set(ANDROID_CLANG_PREFIX "aarch64-linux-android")
else()
    message(FATAL_ERROR "Unsupported Android ABI: ${CMAKE_ANDROID_ARCH_ABI}")
endif()

set(ANDROID_VERSIONED_CLANG "${ANDROID_TOOLCHAIN_ROOT}/bin/${ANDROID_CLANG_PREFIX}${ANDROID_NATIVE_API_LEVEL}-clang")
set(RUST_LIB_DIR "${RUST_TARGET_DIR}/${RUST_TARGET}/release")
set(TARKKA_LIB_PATH "${RUST_LIB_DIR}/libtarkka.so")

# Build the Rust library directly as an Android shared library
add_custom_command(
    OUTPUT ${TARKKA_LIB_PATH}
    COMMAND cd ${TARKKA_ROOT_DIR} && 
            CC=${ANDROID_VERSIONED_CLANG} 
            CARGO_TARGET_${RUST_TARGET_UPPER}_LINKER=${ANDROID_VERSIONED_CLANG}
            cargo build --release --target ${RUST_TARGET}
    DEPENDS ${TARKKA_ROOT_DIR}/src/lib.rs ${TARKKA_ROOT_DIR}/Cargo.toml
    COMMENT "Building Rust library for ${RUST_TARGET}"
)

# Add custom target to build the Rust library and ensure it's included
add_custom_target(build_tarkka ALL DEPENDS ${TARKKA_LIB_PATH}
    COMMAND ${CMAKE_COMMAND} -E copy_if_different 
        ${TARKKA_LIB_PATH}
        ${CMAKE_LIBRARY_OUTPUT_DIRECTORY}/libtarkka.so
    COMMENT "Copying tarkka library to output directory"
)

# Create a simple wrapper library to ensure the tarkka library gets packaged
add_library(tarkka SHARED
    empty.cpp
)

add_dependencies(tarkka build_tarkka)

target_link_libraries(tarkka
    ${TARKKA_LIB_PATH}
    log
)