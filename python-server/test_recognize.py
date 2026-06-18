import base64
import requests

# Ganti dengan path foto salah satu anggota yang ada di dataset
IMAGE_PATH = "dataset/Mahdi Imantaka Sutejo/WIN_20260614_07_21_50_Pro.jpg"

with open(IMAGE_PATH, "rb") as f:
    image_base64 = base64.b64encode(f.read()).decode("utf-8")

response = requests.post(
    "http://localhost:8000/recognize",
    json={"image": image_base64}
)

print(response.json())