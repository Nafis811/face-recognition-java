# Python Face Recognition Server

Server API untuk face recognition berbasis `face_recognition` + FastAPI.
Dikonsumsi oleh Java Application via HTTP REST.

---

## Requirements

- Python 3.14+
- pip

---

## Instalasi

### 1. Install dependencies

pip install face_recognition fastapi uvicorn pillow requests setuptools

### 2. Fix compatibility (wajib untuk Python 3.14)

Buka file berikut:
C:\Users\<nama_user>\AppData\Roaming\Python\Python314\site-packages\face_recognition_models\__init__.py

Ganti baris:
from pkg_resources import resource_filename

Dengan:
from importlib.resources import files
def resource_filename(package, path):
    return str(files(package).joinpath(path))

---

## Struktur Folder

python-server/
├── main.py              # FastAPI app, endpoint /recognize dan /enroll
├── recognizer.py        # Logic face recognition
├── encode_faces.py      # Script generate encodings.pkl dari dataset
├── test_recognize.py    # Script test endpoint
├── encodings.pkl        # Hasil encoding wajah (auto-generated)
├── dataset/             # Foto per anggota
│   ├── Mahdi Imantaka Sutejo/
│   ├── Muhammad Bagas/
│   ├── Muhammad Nafis/
│   └── Muhammad Zayyan/
└── README.md

---

## Cara Menjalankan Server

uvicorn main:app --reload

Server berjalan di: http://localhost:8000

---

## Cara Generate Dataset

### 1. Tambah foto ke folder dataset

Masukkan foto ke folder:
dataset/<nama_anggota>/foto.jpg

Nama folder = nama yang akan muncul di response JSON.

### 2. Jalankan script encoding

python encode_faces.py

Output: encodings.pkl (otomatis dibuat/diperbarui)

---

## API Endpoints

### POST /recognize

Mengenali wajah dari gambar base64.

Request:
{
  "image": "<base64 encoded image>"
}

Response (dikenali):
{
  "status": "recognized",
  "name": "Mahdi Imantaka Sutejo",
  "confidence": 0.87
}

Response (tidak dikenali):
{
  "status": "unknown",
  "name": null,
  "confidence": 0.0
}

### POST /enroll

Menambahkan wajah baru ke sistem.

Request:
{
  "name": "Nama Lengkap",
  "image": "<base64 encoded image>"
}

Response:
{
  "status": "enrolled",
  "name": "Nama Lengkap"
}

---

## Cara Menambah Anggota Baru

1. Buat folder baru di dataset/<nama>/
2. Masukkan 10-15 foto dengan variasi pencahayaan dan sudut
3. Jalankan ulang encode_faces.py
4. Restart server

---

## Catatan

- Threshold confidence default: 0.6 (bisa diubah di recognizer.py)
- encodings.pkl tidak perlu di-commit ke Git — tambahkan ke .gitignore
- Server harus jalan sebelum Java Application dijalankan