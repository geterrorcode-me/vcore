<p align="left">
  <img src="https://img.shields.io/github/stars/geterrorcode-me/vcore?style=social" />
  <img src="https://img.shields.io/github/forks/geterrorcode-me/vcore?style=social" />
  <img src="https://img.shields.io/github/issues/geterrorcode-me/vcore?style=social" />
</p>

<p align="center">
  <img src="https://img.shields.io/github/actions/workflow/status/geterrorcode-me/vcore/android.yml?style=for-the-badge&logo=github&label=Engine%20Build" />
  <img src="https://img.shields.io/github/v/release/geterrorcode-me/vcore?style=for-the-badge&color=orange&logo=github" />
  <img src="https://img.shields.io/badge/License-GPL--3.0-red?style=for-the-badge&logo=opensourceinitiative" />
</p>



<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android%2010--14+-green?style=flat-square&logo=android" />
  <img src="https://img.shields.io/badge/Architecture-ARM64%20%7C%20ARMv7-blue?style=flat-square" />
  <img src="https://img.shields.io/badge/Language-Java%20%7C%20C%2B%2B-orange?style=flat-square&logo=cplusplus" />
  <img src="https://img.shields.io/badge/NDK-r25c%2B-blueviolet?style=flat-square" />
</p>





# VCore Engine (vphone_core)

**VCore** adalah engine virtualisasi runtime Android tingkat lanjut yang dirancang untuk menjalankan aplikasi dalam lingkungan terisolasi (sandbox) tanpa memerlukan instalasi sistem atau root. 

Berbeda dengan cloner biasa, VCore menggunakan arsitektur **Container-based Virtualization** yang menginterupsi komunikasi antara aplikasi target dan Android Framework (AMS, PMS, dan Instrumentation).

## 🚀 Fitur Utama (Current Progress)

* **Native Core Bootstrap:** Inisialisasi engine melalui JNI untuk bypass deteksi tingkat tinggi.
* **PMS Virtualization:** Simulasi `IPackageManager` untuk memberikan identitas virtual pada APK target.
* **Instrumentation Swapping:** Teknik "Identity Theft" pada lifecycle activity untuk menukar komponen host dengan komponen target secara real-time.
* **Stealth Memory Bridge:** Komunikasi antar-proses (IPC) terenkripsi untuk menghindari pelacakan sistem.
* **Anti-Detection Armed:** Melindungi proses virtual dari pengecekan lingkungan (root detection/emulator check).

### 🛡️ Core Security Status
| Component | Security Level | Status |
| :--- | :--- | :--- |
| **Native Bridge** | ![High](https://img.shields.io/badge/-High-red) | ![Armed](https://img.shields.io/badge/-Bypassed-brightgreen) |
| **AMS/ATM Hook** | ![Critical](https://img.shields.io/badge/-Critical-orange) | ![Active](https://img.shields.io/badge/-Functional-blue) |
| **Hidden API** | ![Bypass](https://img.shields.io/badge/-Bypass-blueviolet) | ![Verified](https://img.shields.io/badge/-Verified-success) |
| **Stealth Mode** | ![Stealth](https://img.shields.io/badge/-Stealth-black) | ![Enabled](https://img.shields.io/badge/-Enabled-lightgrey) |


## 🏗️ Arsitektur Teknis

VCore bekerja pada tiga layer utama:
1.  **Native Layer (C/C++):** Hooking pada level sistem (`libc`, `linker`) untuk mengalihkan I/O dan syscall.
2.  **Framework Layer (Java):** Refleksi tingkat lanjut pada `ActivityThread` dan `Singleton<IActivityManager>`.
3.  **Virtual Layer (Stub):** Penggunaan `StubActivity` sebagai wadah (container) bagi lifecycle aplikasi target.

## 🛠️ Cara Penggunaan (Integrasi)

1. Tambahkan `vphone_core.so` ke dalam direktori JNI proyek Anda.
2. Inisialisasi engine pada `MainActivity` Host:
   ```java
   VHookCore.install(context, "com.target.package", "/path/to/target.apk");

  ---




  # ⚠️ DISCLAIMER (PENTING)

> **PROYEK INI DIBUAT HANYA UNTUK TUJUAN PENDIDIKAN DAN PENELITIAN KEAMANAN.**

---

### 🚫 Penyalahgunaan
Pengembang (**geterrorcode-me**) tidak bertanggung jawab atas segala bentuk penyalahgunaan perangkat lunak ini, termasuk namun tidak terbatas pada:
* Pelanggaran hak cipta aplikasi pihak ketiga.
* Tindakan kecurangan (*cheating*).
* Aktivitas ilegal lainnya yang melanggar hukum setempat.

### 🛡️ Risiko Keamanan
Menggunakan teknik *hooking* dan refleksi pada sistem Android dapat menyebabkan ketidakstabilan sistem atau *crash* pada perangkat tertentu. Seluruh risiko penggunaan ditanggung sepenuhnya oleh pengguna (**Use at your own risk**).

### 📑 Pelanggaran TOS
Menjalankan aplikasi di dalam lingkungan virtual mungkin melanggar **Ketentuan Layanan (ToS)** dari aplikasi target. Pastikan Anda memiliki izin tertulis atau legal sebelum melakukan pengujian pada aplikasi pihak ketiga.

### ⚖️ Tanpa Jaminan
Perangkat lunak ini disediakan **"sebagaimana adanya"**, tanpa jaminan apa pun, baik tersurat maupun tersirat, termasuk namun tidak terbatas pada jaminan kelayakan jual atau kesesuaian untuk tujuan tertentu.

---

**© 2026 geterrorcode-me | Built for the Android Underworld.**
