# Hastane İzin Takip Sistemi (Hospital Permission Tracking)

Bu proje, hastane personellerinin izin süreçlerini dijital ortamda yönetmek, takip etmek ve yetkilendirmek amacıyla geliştirilmiş bir **Spring Boot** API uygulamasıdır.

## 🚀 Özellikler

- **Kimlik Doğrulama ve Yetkilendirme:** Spring Security ve JWT (JSON Web Token) tabanlı güvenli giriş sistemi. Kullanıcı (USER) ve Yönetici (ADMIN) rolleri.
- **İzin Yönetimi:** 
  - Kullanıcıların yeni izin talebi oluşturabilmesi.
  - İzin tarihlerinin çakışma kontrolü.
  - Kullanıcıların izin kotalarının (kalan/toplam izin günleri) takibi.
- **Yönetici İş Akışı:** Admin yetkisine sahip kullanıcıların, bekleyen izin taleplerini listeleyip **onaylama** veya **reddetme** işlemleri.
- **Önbellekleme (Caching):** Performans artışı için **Redis** entegrasyonu.
- **Veritabanı Göçü (Migration):** **Flyway** kullanılarak veritabanı şema ve versiyon kontrolü.
- **API Dokümantasyonu:** Springdoc OpenAPI ile otomatik oluşturulan **Swagger UI** arayüzü.
- **Global Exception Handling:** `@ControllerAdvice` kullanılarak hataların standart bir JSON formatında (ErrorResponseDto) dönülmesi.
- **Validasyon:** DTO'lar üzerinde Jakarta Validation ile veri doğrulama.

## 🛠️ Kullanılan Teknolojiler

- **Backend:** Java 21, Spring Boot 3.5.7
- **Güvenlik:** Spring Security, JWT (jjwt)
- **Veritabanı:** PostgreSQL, Spring Data JPA, Hibernate
- **Migration:** Flyway
- **Önbellekleme:** Redis
- **Dokümantasyon:** Springdoc OpenAPI (Swagger)
- **Araçlar:** Maven, Lombok, Thymeleaf

## ⚙️ Kurulum ve Çalıştırma

### Ön Gereksinimler
Projenin lokalinizde çalışabilmesi için aşağıdaki araçların yüklü olması gerekmektedir:
- Java 21 (JDK)
- PostgreSQL (Port: 5433 olarak ayarlanmış, `application.yml` üzerinden değiştirilebilir)
- Redis (Port: 6379, varsayılan)
- Maven

### Adımlar

1. **Projeyi Klonlayın:**
   ```bash
   git clone <repo-url>
   cd permissionTracking
   ```

2. **Veritabanı Ayarları:**
   PostgreSQL üzerinde `permissiondb` adında bir veritabanı oluşturun. Eğer farklı bir kullanıcı adı veya şifre kullanıyorsanız, `src/main/resources/application.yml` dosyasındaki ayarları güncelleyin:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5433/permissiondb
       username: postgres
       password: 1234
   ```

3. **Redis'i Başlatın:**
   Lokalinizde Redis sunucusunun çalıştığından emin olun (Varsayılan: `localhost:6379`).

4. **Projeyi Derleyin ve Çalıştırın:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   *(Flyway, uygulama ayağa kalkarken veritabanı tablolarını otomatik olarak oluşturacaktır.)*

## 📚 API Dokümantasyonu (Swagger)

Uygulama başarıyla başlatıldıktan sonra (varsayılan port **8081**), tüm API uç noktalarını incelemek ve test etmek için Swagger UI arayüzüne gidebilirsiniz:

👉 **[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)**

### Temel API Uç Noktaları

- **Auth & Kullanıcılar (`/api/users`)**
  - `POST /api/users/register` : Yeni kullanıcı kaydı.
  - `POST /api/users/login` : Kullanıcı girişi ve JWT token alımı.
  - `GET /api/users/me` : Giriş yapmış kullanıcının bilgileri.
  - `POST /api/users/{userId}/permissions` : İzin talebi oluşturma.
  - `GET /api/users/{userId}/permissions` : Kullanıcının izin geçmişini listeleme.

- **Yönetici İşlemleri (`/api/admin`)**
  - `GET /api/admin/permissions/pending` : Bekleyen (onaylanmamış) tüm izinleri listeleme.
  - `PATCH /api/admin/permissions/{permissionId}/approve` : İzni onaylama.
  - `PATCH /api/admin/permissions/{permissionId}/reject` : İzni reddetme.

## 🏗️ Proje Yapısı

```
src/main/java/com/hospital/permissiontracking
├── config/       # Security, Swagger, Redis ve JWT konfigürasyonları
├── controller/   # REST API endpoint'leri (AdminController, UserController)
├── dto/          # Veri transfer objeleri (Request/Response)
├── entity/       # Veritabanı varlıkları (User, Permission ve Enum'lar)
├── exception/    # Global hata yakalama (GlobalExceptionHandler)
├── repository/   # Spring Data JPA Repository arayüzleri
└── service/      # İş mantığı (Business Logic) sınıfları
```
