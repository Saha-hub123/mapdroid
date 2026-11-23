# Mapdroid 🗺️
**Aplikasi Android Peta Berbasiskan [MapLibre.org](https://maplibre.org)**

[![Build and Release Mapdroid APK](https://github.com/Saha-hub123/mapdroid/actions/workflows/release.yml/badge.svg)](https://github.com/Saha-hub123/mapdroid/actions/workflows/release.yml)
[![Latest Release](https://img.shields.io/github/v/release/Saha-hub123/mapdroid?color=2563EB&label=Release)](https://github.com/Saha-hub123/mapdroid/releases/latest)
[![Direct APK Download](https://img.shields.io/badge/Download-mapdroid.apk-emerald)](https://github.com/Saha-hub123/mapdroid/releases/latest/download/mapdroid.apk)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📌 Ringkasan Implementasi Tugas

Proyek ini telah mengimplementasikan seluruh poin persyaratan tugas:

1. **GitHub Workflow CI/CD Otomatis (`.github/workflows/release.yml`)**:
   - Begitu dilakukan `git push` ke branch `main`, workflow otomatis berjalan.
   - Melakukan kompilasi release APK Android menggunakan Gradle 8.5 & Java 17.
   - Otomatis membuat Git Tag baru (`v1.0.<run_number>`) dan GitHub Release.
   - Mengunggah berkas `mapdroid.apk` dan `mapdroid-<tag>.apk` yang dapat langsung diunduh dari halaman release.

2. **Landing Page Interaktif (`index.html` & `croot.js`)**:
   - Dibuat menggunakan modul **croot.js** (`jscroot` ES6+ modular CDN).
   - Menampilkan panduan instalasi lengkap untuk pengguna Android.
   - Mengambil data rilis terbaru secara dinamis dari GitHub API untuk menampilkan tautan unduh langsung berkas APK, ukuran file, jumlah unduhan, dan tanggal rilis.
   - Dilengkapi QR Code untuk unduhan langsung dari kamera smartphone.

3. **Aplikasi Android Peta Berbasis MapLibre (`app/`)**:
   - Menggunakan **MapLibre Native Android SDK** (`org.maplibre.gl:android-sdk:11.5.1`).
   - Fitur navigasi peta vektor berkecepatan tinggi.
   - Penanda lokasi (marker) interaktif beserta info koordinat Lintang (Lat) dan Bujur (Lng).
   - Pelacak lokasi GPS real-time (*My Location*) dengan kompas.
   - Multi-style switcher (MapLibre Demo, OpenFreeMap Bright, Liberty, Positron).
   - Antarmuka modern Material Design 3.

---

## ⬇️ Unduh Berkas APK Rilis Terakhir

Anda dapat langsung mengunduh berkas APK versi terbaru tanpa perlu kompilasi lokal:

* 📥 **[Unduh mapdroid.apk (Versi Terbaru)](https://github.com/Saha-hub123/mapdroid/releases/latest/download/mapdroid.apk)**
* 📦 **[Daftar Seluruh Rilis di GitHub](https://github.com/Saha-hub123/mapdroid/releases)**

---

## 🌐 Landing Page & Panduan Web

Buka berkas `index.html` di browser Anda atau host melalui GitHub Pages:
* Tampilan responsif dengan Tailwind CSS.
* Panduan instalasi langkah demi langkah.
* Fitur salin tautan APK dan QR Code scanner.

---

## 📱 Panduan Instalasi APK di Android

1. **Unduh APK**: Klik tautan download `mapdroid.apk` di atas atau melalui landing page.
2. **Buka File**: Buka notifikasi selesai unduh atau buka folder *Download* di File Manager HP Anda.
3. **Izinkan Instalasi**: Jika muncul peringatan keamanan, klik *Pengaturan* lalu centang **"Izinkan dari sumber ini" (Allow from unknown sources)**.
4. **Pasang**: Tekan tombol **Install**.
5. **Jalankan**: Buka aplikasi **Mapdroid** dan izinkan akses lokasi (GPS) untuk navigasi.

---

## 📂 Struktur Proyek

```
mapdroid/
├── .github/
│   └── workflows/
│       └── release.yml          # GitHub Actions Auto Tag & Release APK
├── app/
│   ├── build.gradle             # Konfigurasi modul Android & dependensi MapLibre
│   ├── proguard-rules.pro       # Aturan ProGuard untuk MapLibre
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/org/mapdroid/
│           │   └── MainActivity.kt    # Logika peta MapLibre, GPS & interaksi
│           └── res/
│               ├── drawable/          # Vektor icon (lokasi, zoom, marker, layer)
│               ├── layout/
│               │   └── activity_main.xml  # Layout UI Material Design 3
│               └── values/            # strings, colors, themes
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle                 # Konfigurasi Gradle root
├── settings.gradle              # Repositori Google, MavenCentral, GradlePortal
├── gradlew                      # Gradle wrapper script (Linux/macOS)
├── gradlew.bat                  # Gradle wrapper script (Windows)
├── croot.js                     # Skrip kontroler croot.js untuk landing page
├── index.html                   # Landing page modern & panduan unduh
├── README.md                    # Dokumentasi proyek
└── LICENSE                      # Lisensi MIT
```

---

## 🛠️ Spesifikasi Teknis

* **Bahasa**: Kotlin 1.9.22 & Java 17
* **Android Target**: Android 14 (API 34)
* **Minimum Android**: Android 7.0 (API 24)
* **Map Engine**: MapLibre Native SDK Android 11.5.1
* **CI/CD**: GitHub Actions (Runner `ubuntu-latest`)
* **Frontend**: CrootJS (`jscroot/lib@0.2.8`) & Tailwind CSS

---

## 📄 Lisensi

Proyek ini dilisensikan di bawah [MIT License](LICENSE).
