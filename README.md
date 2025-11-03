# TugisLine CAD - Çizim Programı

TugisLine, web tarayıcısında çalışan modern bir CAD (Computer-Aided Design) çizim programıdır. HTML5 Canvas teknolojisi kullanılarak geliştirilmiştir.

## Özellikler

- **Çizim Araçları**
  - Çizgi çizme
  - Dikdörtgen çizme
  - Daire çizme
  - Çokgen (polygon) çizme
  - Seçme ve düzenleme

- **Görünüm Özellikleri**
  - Yakınlaştırma/Uzaklaştırma (zoom)
  - Kaydırma (pan)
  - Izgara görünümü
  - Izgaraya yakalama (snap to grid)

- **Düzenleme Özellikleri**
  - Nesneleri seçme
  - Nesne özelliklerini düzenleme (renk, kalınlık, dolgu)
  - Nesneleri silme
  - Tüm çizimi temizleme

- **Dosya İşlemleri**
  - Çizimi JSON formatında kaydetme
  - Kaydedilmiş çizimleri yükleme

## Kullanım

### Başlangıç

1. `index.html` dosyasını bir web tarayıcısında açın
2. Çizim yapmak için araç çubuğundan bir araç seçin

### Araçlar

- **Seç (S)**: Nesneleri seçmek ve düzenlemek için
- **Çizgi (L)**: Çizgi çizmek için tıklayın ve sürükleyin
- **Dikdörtgen (R)**: Dikdörtgen çizmek için tıklayın ve sürükleyin
- **Daire (C)**: Daire çizmek için merkez noktasına tıklayın ve sürükleyin
- **Çokgen (P)**: Her köşe için tıklayın, bitirmek için çift tıklayın

### Klavye Kısayolları

- **S**: Seç aracı
- **L**: Çizgi aracı
- **R**: Dikdörtgen aracı
- **C**: Daire aracı
- **P**: Çokgen aracı
- **Delete**: Seçili nesneyi sil
- **+ / =**: Yakınlaş
- **-**: Uzaklaş
- **0**: Görünümü sıfırla
- **Esc**: Çizimi iptal et
- **Shift + Sol Tık**: Kaydırma (pan)

### Görünüm Kontrolleri

- **Kaydırma**: Shift tuşunu basılı tutup sürükleyin veya orta fare tuşunu kullanın
- **Yakınlaştırma**: Fare tekerleğini kullanın veya +/- tuşlarına basın
- **Sıfırla**: Görünümü varsayılan konuma ve yakınlaştırmaya döndürür

### Özellikler Paneli

Sağ taraftaki özellikler panelinden:
- Çizgi rengi
- Çizgi kalınlığı
- Dolgu rengi
- Dolgu açık/kapalı
- Seçili nesne bilgileri

düzenlenebilir.

### Dosya İşlemleri

- **Kaydet**: Çizimi JSON dosyası olarak indirir
- **Yükle**: Kaydedilmiş bir JSON dosyasını açar

## Teknolojiler

- HTML5
- CSS3
- JavaScript (ES6+)
- Canvas API

## Tarayıcı Desteği

Modern web tarayıcılarında çalışır:
- Chrome/Edge 90+
- Firefox 88+
- Safari 14+

## Lisans

MIT License
