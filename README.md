# Rebirth (Fabric 1.21.11)

Animasi rebirth: kelopak mata membuka -> flash putih -> kamera orang ketiga, lalu kembali normal.

## Build
Cara paling aman: buat template resmi di https://fabricmc.net/develop
(Minecraft 1.21.11, Yarn, Java 21), lalu timpa folder `src/` dengan folder `src/` dari sini.
Atau pakai langsung semua file di sini: jalankan `gradle build` (butuh Gradle 9.x + JDK 21).
Hasil jar ada di `build/libs/rebirth-1.0.0.jar`. Taruh di folder mods bersama Fabric API.

## Pakai
/rebirth                 untuk diri sendiri
/rebirth <pemain>        untuk pemain lain
/rebirth <pemain> <tick> durasi sendiri (20-1200 tick, 20 tick = 1 detik)

Mod harus terpasang di server DAN di client pemain.

## Build jar tanpa komputer (GitHub Actions)
1. Buat repository baru di github.com (bisa dari HP), upload SEMUA isi folder ini
   (termasuk folder tersembunyi `.github`).
2. Buka tab "Actions" -> tunggu workflow "Build jar" selesai (hijau).
3. Buka run tersebut -> bagian "Artifacts" -> unduh `rebirth-jar`.
4. Di dalam zip itu pakai `rebirth-1.0.0.jar` (BUKAN yang `-sources`).
Jika build gagal, salin pesan error dari log Actions dan kirim ke saya.
