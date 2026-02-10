# Eshop (Spring Boot) – Reflection
---
## Refleksi 1
---
### Prinsip Clean Code yang Diimplementasikan
- **Separation of Concerns**
  - **Controller** menangani routing HTTP (`/product/create`, `/product/list`, `/product/edit`, `/product/delete`).
  - **Service** berperan sebagai perantara logika bisnis agar controller tetap sederhana.
  - **Repository** bertanggung jawab pada penyimpanan dan manipulasi data produk (in-memory list).
- **Penamaan konsisten**
  - Nama class, method, dan variabel mencerminkan fungsinya (`create`, `findAll`, `findById`, `edit`, `delete`).
- **Single Responsibility**
  - Setiap class memiliki tanggung jawab tunggal sesuai layer-nya.
- **Readability**
  - Alur request mudah diikuti dari controller → service → repository.

---

### Secure Coding Practices yang Diterapkan
- **ID unik dibuat di backend**
  - `productId` di-generate di backend (UUID) sehingga user tidak bisa memanipulasi ID dari form.
- **Minim exposure data**
  - Halaman hanya menampilkan informasi yang diperlukan (nama dan kuantitas produk).
- **Kontrol HTTP method**
  - Operasi delete menggunakan metode **POST** melalui form, bukan GET.

---

### Masalah yang Ditemukan dan Perbaikannya
- **Homepage tidak muncul di root (`/`)**
  - Penyebab: mapping berada di controller dengan prefix `/product`.
  - Perbaikan: menambahkan handler khusus root (`/`) yang mengarah ke halaman homepage.
- **Edit/Delete gagal karena `productId` kosong**
  - Penyebab: `productId` tidak diisi dari form create.
  - Perbaikan: generate `productId` di backend saat create.
- **Whitelabel Error Page**
  - Penyebab: template tidak ditemukan atau URL yang diakses tidak sesuai mapping.
  - Perbaikan: menyamakan nama return view dengan nama file HTML di folder `templates`.

---

### Problem yang Dihadapi dan Cara Mengatasinya
- **Branch di local lebih banyak daripada di GitHub**
  - Penyebab: branch lokal belum di-push ke remote.
  - Solusi: push branch yang diperlukan ke GitHub menggunakan `git push`.
- **Port 8080 sudah digunakan**
  - Penyebab: proses Java lain masih berjalan.
  - Solusi: cek proses dengan `lsof` lalu hentikan proses yang menggunakan port tersebut.
- **Redirect relatif menyebabkan bisa salah tujuan**
  - Penggunaan redirect:list bersifat relatif terhadap path saat ini, sehingga dari /product/edit/{id} bisa menjadi /product/edit/list.
  - Solusi: gunakan redirect langsung seperti redirect:/product/list.
- **Belum ada validasi input**
  - productName bisa kosong dan productQuantity bisa negatif bila tidak divalidasi.
  - Solusi: menambahkan Bean Validation pada model dan menggunakan @Valid di controller.

---
