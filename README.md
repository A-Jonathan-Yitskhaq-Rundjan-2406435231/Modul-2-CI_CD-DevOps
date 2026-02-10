# Eshop (Spring Boot) – Reflection
---
## Refleksi 1
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

## Reflection 2

### 1. Setelah menulis unit test, bagaimana perasaanmu? Berapa banyak unit test yang sebaiknya dibuat dalam satu class? Bagaimana memastikan unit test sudah cukup? Apakah 100% code coverage berarti tidak ada bug?

Setelah menulis unit test, saya merasa lebih tenang karena perubahan kecil pada kode bisa langsung diverifikasi tanpa harus selalu menjalankan aplikasi secara manual. Unit test juga membantu saya memahami fungsi dari tiap method/kelas: input apa yang valid dan output apa yang diharapkan. Selain itu, membantu saya untuk mengecek kesalahan pada function yang sudah dibuat. 

Jumlah unit test dalam satu class tidak ada angka “pasti”, tetapi sebaiknya mengikuti prinsip berikut:
- **Satu test class untuk satu class yang diuji** (misalnya `ProductTest` untuk `Product`).
- **Banyaknya test case ditentukan oleh perilaku (behavior) yang perlu diverifikasi**, bukan oleh jumlah baris kode. Minimal mencakup:
  - skenario normal (happy path),
  - skenario batas (edge cases),
  - skenario gagal (invalid input / kondisi tidak ditemukan).
- Jika jumlah test case mulai terlalu banyak atau mencampur banyak tanggung jawab, test bisa dipisah berdasarkan fitur/kelompok behavior (misalnya `ProductRepositoryCreateTest`, `ProductRepositoryDeleteTest`) agar tetap terbaca.

Untuk memastikan unit test “cukup”, saya tidak hanya mengandalkan feeling, tetapi juga:
- Mengecek **requirements / acceptance criteria** lalu memastikan tiap requirement punya test yang relevan.
- Menguji **branch/condition penting** (misalnya kondisi product ditemukan vs tidak ditemukan).
- Menggunakan **code coverage** sebagai indikator: seberapa banyak kode yang benar-benar dieksekusi oleh test.

Namun, **100% code coverage tidak berarti tidak ada bug**. Coverage hanya menunjukkan “kode pernah dijalankan oleh test”, bukan berarti:
- assert-nya sudah benar,
- semua kombinasi input penting sudah diuji,
- semua bug logika tertangkap,
- semua integrasi antar komponen aman.
Contoh: test bisa menjalankan method tapi tidak memverifikasi output dengan assert yang tepat, sehingga coverage tinggi tetapi bug tetap lolos. Karena itu, coverage sebaiknya dipakai sebagai **alat bantu**, bukan jaminan kualitas.

---

### 2) Jika membuat functional test baru untuk memverifikasi jumlah item di product list, bagaimana dampaknya terhadap clean code? Apa potensi masalah dan perbaikannya?

Jika saya membuat test suite baru yang “mirip” dengan functional test sebelumnya (setup yang sama, instance variable yang sama), maka ada risiko **duplikasi kode**. Walaupun test bertambah, kualitas kode bisa turun jika struktur test menjadi berulang dan sulit dipelihara.

**Potensi clean code issues:**
1. **Duplication (DRY violation)**
   - Setup WebDriver, base URL, login/akses halaman, dan inisialisasi variabel yang sama di-copy ke class test baru.
   - Dampak: bila ada perubahan kecil (misalnya route homepage berubah), saya harus mengubah banyak file test sekaligus.

2. **Low maintainability & readability**
   - Banyak boilerplate yang sama membuat inti test (assert yang penting) “tenggelam”.
   - Test jadi lebih sulit dibaca dan dipahami cepat.

3. **Tight coupling terhadap detail UI**
   - Jika locator/selector (id/class/xpath) ditulis berulang di banyak test, perubahan kecil di HTML bisa merusak banyak test.
   - Ini membuat test rapuh (flaky) dan meningkatkan effort maintenance.

4. **Inconsistent test data & hidden dependencies**
   - Jika tiap test suite membuat data produk dengan cara berbeda-beda, urutan eksekusi atau state aplikasi bisa memengaruhi hasil test.
   - Ini berisiko menyebabkan test kadang lulus/kadang gagal.

**Rekomendasi perbaikan agar lebih bersih:**
1. **Extract common setup ke base class / utility**
   - Buat abstract class misalnya `BaseFunctionalTest` yang berisi:
     - setup dan teardown WebDriver,
     - helper untuk open page,
     - helper untuk create product.
   - Test baru cukup fokus pada assertion inti.

2. **Gunakan helper methods yang deskriptif**
   - Contoh: `createProduct(name, qty)`, `openProductList()`, `getProductCount()`.
   - Ini meningkatkan readability dan mengurangi duplikasi.

3. **Pertimbangkan Page Object Model (POM)**
   - Pisahkan detail selector DOM ke class khusus page, misalnya:
     - `HomePage`, `CreateProductPage`, `ProductListPage`.
   - Test menjadi lebih stabil dan mudah diperbaiki jika UI berubah.

4. **Gunakan test data yang konsisten dan isolasi state**
   - Pastikan setiap test tidak bergantung pada hasil test lain.
   - Jika memungkinkan, reset state sebelum test, atau buat produk yang unik (misalnya nama berawalan timestamp/UUID) agar tidak bentrok.

Dengan perbaikan di atas, menambah functional test suite tidak akan menurunkan kualitas kode, karena struktur test tetap rapi, minim duplikasi, dan mudah dipelihara.