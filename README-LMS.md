# NUSA LMS dengan Face Recognition Login

Proyek ini menggabungkan aplikasi JavaFX, SQLite, dan layanan face recognition Python. Wajah yang berhasil dikenali digunakan sebagai identitas login, lalu pengguna diarahkan ke dashboard LMS sederhana.

## Fitur

- Login menggunakan wajah dengan batas kecocokan minimal 60%
- Sesi pengguna selama aplikasi berjalan
- Dashboard ringkasan mata kuliah, tugas, dan progres belajar
- Pencatatan login berhasil ke SQLite
- Enroll wajah dan riwayat tetap tersedia
- Logout untuk menghapus sesi dan kembali ke halaman login

## Menjalankan aplikasi

1. Buka terminal pada folder `python-server`.
2. Jalankan `uvicorn main:app --reload`.
3. Pastikan kamera tidak sedang dipakai aplikasi lain.
4. Buka folder `UI` di IntelliJ IDEA dan jalankan kelas `HelloApplication`.

Alternatif melalui terminal pada folder `UI`:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"
.\mvnw.cmd javafx:run
```

Pada penggunaan pertama, Maven Wrapper memerlukan koneksi internet untuk mengambil komponen build.

## Konsep OOP yang digunakan

- **Encapsulation:** data sesi dan model dibungkus dalam kelas masing-masing.
- **Abstraction:** `CameraService`, `RecognitionClient`, dan repository berupa interface.
- **Polymorphism:** implementasi real/mock dapat dipertukarkan melalui interface.
- **Separation of concerns:** model, controller, service, dan repository dipisahkan.
- **Singleton:** `UserSession` menyimpan satu pengguna aktif.

Data mata kuliah dan tugas pada dashboard masih berupa data contoh agar ruang lingkup LMS tetap sederhana.
