# UWL Campus Navigator 🏛️

A Java Swing application using **Dijkstra's Algorithm** to calculate and visualize the shortest walking paths between buildings at the University of Wisconsin-La Crosse.

### 📍 Purpose
I designed this for **personal use** to optimize my daily commute between classes. As a CS student, I wanted a precise tool to map out the best shortcuts between the **Wing Technology Center** labs, **Centennial Hall** lectures, and the **Student Union**.

### ✨ Key Features
* **Pathfinding Engine:** Real-time shortest path calculation using weighted graphs and Adjacency Lists.
* **Toggle Modes:** *
   * **User Mode:** Clean, minimal navigation interface.
   * **Technical Mode:** Press **'T'** to reveal the waypoint network, node labels, and edge connections.
* **Animated UI:** Smooth, segment-based path rendering.
* **Responsive Scaling:** Custom coordinate mapping ensures accuracy across all window sizes.

### 🛠️ Tech Stack
* **Language:** Java
* **Graphics:** Swing & AWT (Graphics2D)
* **Algorithm:** Dijkstra’s Shortest Path ($O(E \log V)$)

### 🚀 How to Run
1. Ensure `campus_map.png` is in the root directory.
2. **Compile & Run:**
   ```bash
   javac src/CampusNavigator.java
   java -cp src CampusNavigator
