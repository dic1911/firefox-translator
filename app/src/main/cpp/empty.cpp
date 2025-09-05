// Minimal C++ wrapper to ensure the Rust library is included in the APK
// This is needed because CMake IMPORTED libraries aren't automatically 
// packaged by Android's build system

extern "C" {
    // This forces the linker to include the Rust library symbols
    __attribute__((constructor))
    void ensure_tarkka_linked() {
        // Empty constructor ensures the library is loaded
    }
}