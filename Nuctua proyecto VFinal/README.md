# 👟 Nuctua VFinal – Plataforma E-Commerce de Calzado Exclusivo

Prototipo interactivo de alta fidelidad diseñado en **Figma** para la aplicación e-commerce de calzado exclusivo **Nuctua (Versión Final)**. Esta versión depura y cierra el alcance del proyecto en un **flujo aprobable de 27 vistas interconectadas**: 3 de acceso, 8 de la experiencia del cliente, 8 ventanas emergentes (popups) de confirmación y error, y 8 del portal de administración (**Backoffice**), con más de 120 interacciones de prototipo.

---

## 👥 Integrantes del Equipo
* **Luis Felipe Pachón Negrete**
* **Shaiel Andrés Ricardo Conde**
* **Junior Germán Varilla Teherán**

---

## 🔗 Enlaces del Prototipo en Figma y Repositorio
* 📦 **Repositorio Oficial GitHub:** [https://github.com/Felipppv/Prototipo-nuctua](https://github.com/Felipppv/Prototipo-nuctua)
* 🚀 **Prototipo Interactivo (Modo Presentación):** [Probar prototipo interactivo en vivo](https://www.figma.com/proto/2WViH7QbT6c7dnc49OkTZt/Nuctua-%E2%80%93-Inicio--Registro-e-Historiales?node-id=12-2455&p=f&scaling=scale-down&content-scaling=fixed&page-id=0%3A1&starting-point-node-id=12%3A2455&show-proto-sidebar=1)
* 🎨 **Lienzo de Diseño (Modo Edición):** [Ver lienzo de trabajo en Figma](https://www.figma.com/design/2WViH7QbT6c7dnc49OkTZt/Nuctua-%E2%80%93-Inicio--Registro-e-Historiales?node-id=0-1&p=f&t=E8qSV0LAsylXOWSN-0)

> Página de Figma: **"Nuctua V3 – flujo aprobable"**. Puntos de inicio del prototipo: *01 Login cliente*, *02 Registro de usuario* y *A1 Admin / Inventario*.

---

## 📌 Descripción del Proyecto (Evolución VFinal)
**Nuctua VFinal** toma la arquitectura dual de la V2 (tienda + backoffice) y la convierte en un flujo lineal, coherente y listo para aprobación. Las colecciones separadas de la V2 (Inicio, Novedades, Hombre, Mujer, Calle) se unifican en un único **Catálogo** con buscador y filtros, se agrega una vista de **Detalle de producto**, y todos los mensajes del sistema pasan a ser **popups** independientes. Todo el catálogo usa **12 pares reales** (fotos de Wikimedia Commons con licencia libre) con SKU, precio y stock consistentes en todas las pantallas.

Identidad visual **neo-brutalista editorial**: fondo crema `#FFF4E4` / `#FFFBF3`, alto contraste café-negro `#1B1410`, acento naranja `#FF6A2B` y amarillo `#FFC24B` para etiquetas; titulares en **Anton** y textos en **Space Grotesk**.

1. **Acceso (3 vistas):** Login de cliente, registro de usuario y login de administrador (redirige según el rol).
2. **Cliente (8 vistas):** Catálogo con buscador → Detalle de producto → Carrito → Pago (checkout) → Historial de compras, además de Lista de deseos, Perfil de usuario y Buscar zapatos.
3. **Popups (8 vistas):** Ordenar catálogo, Agregado al carrito, Compra confirmada, Credenciales incorrectas, Eliminar producto, Cambiar estado del pedido, Cuenta creada y Cambios guardados. Cada una indica sobre qué pantalla se abre.
4. **Administrador (8 vistas):** Inventario → Stock por talla, Productos → Registrar producto, Pedidos, Clientes, Buscar y Perfil del administrador.

### 🔄 Cambios respecto a la V2
| V2 | VFinal |
|----|--------|
| 22 vistas | 27 vistas (19 pantallas + 8 popups) |
| Inicio + 4 colecciones separadas (Novedades, Hombre, Mujer, Calle) | Un solo **Catálogo** con pestañas por marca (Dunk/SB, Jordan, Yeezy, Balenciaga, Off-White) y filtros |
| Sin vista de producto | Nueva vista **C2 Detalle de producto** con selector de talla, stock por talla y "Vistos recientemente" |
| Mensajes de sistema dentro de las pantallas | **8 popups** independientes de confirmación, error y acción destructiva |
| Productos genéricos | **12 pares reales** con SKU (NCT-001 a NCT-012), precio y stock consistentes |
| Crear o editar producto | **A4 Registrar producto** con stock por talla y cálculo automático del stock total |

---

## 📸 Capturas de Pantalla (27 Vistas)

### 🔐 1. Acceso
#### 01 Login cliente
![Login cliente](./screenshots/01%20Login%20cliente%20Nuctua.png)

#### 02 Registro de usuario
![Registro de usuario](./screenshots/02%20Registro%20de%20usuario%20Nuctua.png)

#### 03 Login administrador
![Login administrador](./screenshots/03%20Login%20administrador%20Nuctua.png)

---

### 🛍️ 2. Cliente
#### C1 Catálogo (con buscador)
![Catálogo](./screenshots/C1%20Catalogo%20Nuctua.png)

#### C2 Detalle de producto
![Detalle de producto](./screenshots/C2%20Detalle%20de%20producto%20Nuctua.png)

#### C3 Carrito
![Carrito](./screenshots/C3%20Carrito%20Nuctua.png)

#### C4 Pago (checkout)
![Pago](./screenshots/C4%20Pago%20Nuctua.png)

#### C5 Historial de compras
![Historial de compras](./screenshots/C5%20Historial%20de%20compras%20Nuctua.png)

#### C6 Lista de deseos
![Lista de deseos](./screenshots/C6%20Lista%20de%20deseos%20Nuctua.png)

#### C7 Perfil de usuario
![Perfil de usuario](./screenshots/C7%20Perfil%20de%20usuario%20Nuctua.png)

#### C8 Buscar zapatos
![Buscar zapatos](./screenshots/C8%20Buscar%20zapatos%20Nuctua.png)

---

### 💬 3. Popups (ventanas emergentes)
#### P1 Ordenar catálogo — se abre sobre C1 Catálogo
![Ordenar catálogo](./screenshots/P1%20Popup%20Ordenar%20catalogo%20Nuctua.png)

#### P2 Agregado al carrito — se abre sobre C2 Detalle
![Agregado al carrito](./screenshots/P2%20Popup%20Agregado%20al%20carrito%20Nuctua.png)

#### P3 Compra confirmada — se abre sobre C4 Pago
![Compra confirmada](./screenshots/P3%20Popup%20Compra%20confirmada%20Nuctua.png)

#### P4 Credenciales incorrectas — se abre sobre 01 / 03 Login
![Credenciales incorrectas](./screenshots/P4%20Popup%20Credenciales%20incorrectas%20Nuctua.png)

#### P5 Eliminar producto — se abre sobre A3 Productos / A1 Inventario
![Eliminar producto](./screenshots/P5%20Popup%20Eliminar%20producto%20Nuctua.png)

#### P6 Cambiar estado del pedido — se abre sobre A5 Pedidos
![Cambiar estado del pedido](./screenshots/P6%20Popup%20Cambiar%20estado%20del%20pedido%20Nuctua.png)

#### P7 Cuenta creada — se abre sobre 02 Registro
![Cuenta creada](./screenshots/P7%20Popup%20Cuenta%20creada%20Nuctua.png)

#### P8 Cambios guardados — se abre sobre A2 Stock por talla
![Cambios guardados](./screenshots/P8%20Popup%20Cambios%20guardados%20Nuctua.png)

---

### 🛠️ 4. Administrador (Backoffice)
#### A1 Inventario
![Inventario](./screenshots/A1%20Admin%20Inventario%20Nuctua.png)

#### A2 Stock por talla
![Stock por talla](./screenshots/A2%20Admin%20Stock%20por%20talla%20Nuctua.png)

#### A3 Productos
![Productos](./screenshots/A3%20Admin%20Productos%20Nuctua.png)

#### A4 Registrar producto
![Registrar producto](./screenshots/A4%20Admin%20Registrar%20producto%20Nuctua.png)

#### A5 Pedidos
![Pedidos](./screenshots/A5%20Admin%20Pedidos%20Nuctua.png)

#### A6 Clientes
![Clientes](./screenshots/A6%20Admin%20Clientes%20Nuctua.png)

#### A7 Buscar
![Buscar](./screenshots/A7%20Admin%20Buscar%20Nuctua.png)

#### A8 Perfil del administrador
![Perfil del administrador](./screenshots/A8%20Admin%20Perfil%20Nuctua.png)

---

## 📷 Créditos de Imágenes
Todas las fotos de producto son reales, tomadas de **Wikimedia Commons** con licencia libre (CC0, CC BY y CC BY-SA). El detalle de cada archivo y su licencia está en el frame **"Banco de fotos y créditos"** del archivo de Figma.

---

## 📁 Estructura del Repositorio
```text
Nuctua-Proyecto VFinal/
├── screenshots/
│   ├── 01 Login cliente Nuctua.png
│   ├── 02 Registro de usuario Nuctua.png
│   ├── 03 Login administrador Nuctua.png
│   ├── C1 Catalogo Nuctua.png
│   ├── C2 Detalle de producto Nuctua.png
│   ├── C3 Carrito Nuctua.png
│   ├── C4 Pago Nuctua.png
│   ├── C5 Historial de compras Nuctua.png
│   ├── C6 Lista de deseos Nuctua.png
│   ├── C7 Perfil de usuario Nuctua.png
│   ├── C8 Buscar zapatos Nuctua.png
│   ├── P1 Popup Ordenar catalogo Nuctua.png
│   ├── P2 Popup Agregado al carrito Nuctua.png
│   ├── P3 Popup Compra confirmada Nuctua.png
│   ├── P4 Popup Credenciales incorrectas Nuctua.png
│   ├── P5 Popup Eliminar producto Nuctua.png
│   ├── P6 Popup Cambiar estado del pedido Nuctua.png
│   ├── P7 Popup Cuenta creada Nuctua.png
│   ├── P8 Popup Cambios guardados Nuctua.png
│   ├── A1 Admin Inventario Nuctua.png
│   ├── A2 Admin Stock por talla Nuctua.png
│   ├── A3 Admin Productos Nuctua.png
│   ├── A4 Admin Registrar producto Nuctua.png
│   ├── A5 Admin Pedidos Nuctua.png
│   ├── A6 Admin Clientes Nuctua.png
│   ├── A7 Admin Buscar Nuctua.png
│   └── A8 Admin Perfil Nuctua.png
├── Nuctua VFinal.fig
├── Informe Prototipo Nuctua VFinal.docx
└── README.md
```
