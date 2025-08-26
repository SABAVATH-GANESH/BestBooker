# 🚖 BestBooker

BestBooker is an Android ride-booking aggregator app that helps users compare fares across **Uber, Ola, and Rapido** and choose the best option.  
Currently, since official APIs are not publicly available, **Room Database** is used for fare estimation.  
The app also supports **Uber deep linking** for booking and integrates the **Google Places API** for searching pickup/drop locations.

---

## ✨ Features
- 🔎 Search and select pickup/drop locations using **Google Places API**.
- 📍 View routes and nearby locations on **Google Maps SDK**.
- 💰 Compare fares across Uber, Ola, and Rapido (using Room DB simulation).
- 🚖 **Uber deep link support** → redirect directly to the Uber app for booking.
- 🖼️ Simple and intuitive UI for ride selection.
- 🏗️ Extensible architecture to support future API integration and auto-booking.

---

## 🛠 Tech Stack
- **Kotlin** – Modern Android app development  
- **Android Studio** – Development environment  
- **Room Database** – Local storage & fare simulation  
- **Google Maps SDK** – Map and route visualization  
- **Google Places API** – Location search (pickup/drop)  
- **Deep Links** – Open Uber app directly from BestBooker  

---

## 🚀 Getting Started

### 1. Clone the repository
```bash
git clone https://github.com/SABAVATH-GANESH/BestBooker.git
