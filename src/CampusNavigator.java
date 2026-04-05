import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.io.File;
import java.io.IOException;

// --- Data Structures ---
class Edge {
    public final Node target;
    public final double weight;

    public Edge(Node target, double weight) {
        this.target = target;
        this.weight = weight;
    }
}

class Node implements Comparable<Node> {
    public final String name;
    public final int x, y;
    public List<Edge> adjacencies = new ArrayList<>();
    public double minDistance = Double.POSITIVE_INFINITY;
    public Node previous;

    public Node(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public void reset() {
        this.minDistance = Double.POSITIVE_INFINITY;
        this.previous = null;
    }

    @Override
    public int compareTo(Node other) {
        return Double.compare(minDistance, other.minDistance);
    }
}

public class CampusNavigator extends JPanel {
    private List<Node> allNodes = new ArrayList<>();
    private Node startNode = null;
    private Node endNode = null;
    private List<Node> shortestPath = new ArrayList<>();
    private Image mapImage;
    private final Color UWL_MAROON = new Color(128, 0, 0);
    private boolean techMode = false;

    // Animation & Scaling Constants
    private double animationProgress = 0.0;
    private Timer animationTimer;
    private final double ORIGINAL_WIDTH = 1366.0;
    private final double ORIGINAL_HEIGHT = 768.0;

    public CampusNavigator() {
        try {
            mapImage = ImageIO.read(new File("campus_map.png"));
        } catch (IOException e) {
            System.err.println("Could not find campus_map.png");
        }

        setupGraph();

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_T) {
                    techMode = !techMode;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Node clicked = findClickedNode(e.getX(), e.getY());
                if (clicked != null) {
                    if (startNode == null || (startNode != null && endNode != null)) {
                        startNode = clicked;
                        endNode = null;
                        shortestPath.clear();
                        resetDistances();
                        animationProgress = 0.0;
                        if (animationTimer != null) animationTimer.stop();
                    } else {
                        endNode = clicked;
                        runDijkstra(startNode);
                        shortestPath = getShortestPathTo(endNode);

                        animationProgress = 0.0;
                        if (animationTimer != null) animationTimer.stop();
                        animationTimer = new Timer(15, ae -> {
                            animationProgress += 0.02;
                            if (animationProgress >= 1.0) {
                                animationProgress = 1.0;
                                animationTimer.stop();
                            }
                            repaint();
                        });
                        animationTimer.start();
                    }
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(findClickedNode(e.getX(), e.getY()) != null ?
                        new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    private void resetDistances() {
        for (Node n : allNodes) n.reset();
    }

    private List<Node> getShortestPathTo(Node target) {
        List<Node> path = new ArrayList<>();
        for (Node node = target; node != null; node = node.previous) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }

    private void runDijkstra(Node source) {
        resetDistances();
        source.minDistance = 0;
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(source);

        while (!queue.isEmpty()) {
            Node u = queue.poll();
            for (Edge e : u.adjacencies) {
                double dist = u.minDistance + e.weight;
                if (dist < e.target.minDistance) {
                    queue.remove(e.target);
                    e.target.minDistance = dist;
                    e.target.previous = u;
                    queue.add(e.target);
                }
            }
        }
    }

    private Node findClickedNode(int mouseX, int mouseY) {
        double scaleX = getWidth() / ORIGINAL_WIDTH;
        double scaleY = getHeight() / ORIGINAL_HEIGHT;
        for (Node n : allNodes) {
            if (n.name.startsWith("w")) continue;
            if (Math.hypot((n.x * scaleX) - mouseX, (n.y * scaleY) - mouseY) < 20) return n;
        }
        return null;
    }

    private void addEdge(Node a, Node b) {
        double dist = Math.hypot(a.x - b.x, a.y - b.y);
        a.adjacencies.add(new Edge(b, dist));
        b.adjacencies.add(new Edge(a, dist));
    }

    private void setupGraph() {
        // --- Building Nodes ---
        Node eagleD1 = new Node("Eagle Door 1", 435, 150);
        Node eagleD2 = new Node("Eagle Door 2", 400, 105);
        Node whitney = new Node("Whitney Center", 345, 240);
        Node recD1   = new Node("REC Door 1", 542, 183);
        Node recD2   = new Node("REC Door 2", 541, 288);
        Node centD1  = new Node("Centennial D1", 593, 502);
        Node centD2  = new Node("Centennial D2", 600, 601);
        Node centD3  = new Node("Centennial D3", 688, 567);
        Node unionD1 = new Node("Union Door 1", 811, 275);
        Node unionD2 = new Node("Union Door 2", 894, 217);
        Node unionD3 = new Node("Union Door 3", 855, 177);
        Node soccer  = new Node("Soccer Bldg", 1128, 150);
        Node vet1    = new Node("Veterans Ent 1", 1067, 252);
        Node vet2    = new Node("Veterans Ent 2", 1068, 404);
        Node hetzel  = new Node("Hetzel Fieldhouse", 1258, 407);
        Node wing    = new Node("Wing Tech", 752, 702);

        // --- Waypoints ---
        Node w1 = new Node("w1", 374, 173);   Node w2 = new Node("w2", 375, 307);
        Node w3 = new Node("w3", 394, 400);   Node w4 = new Node("w4", 541, 475);
        Node w5 = new Node("w5", 545, 630);   Node w6 = new Node("w6", 734, 633);
        Node w7 = new Node("w7", 940, 629);   Node w8 = new Node("w8", 919, 481);
        Node w9 = new Node("w9", 715, 484);   Node w10 = new Node("w10", 911, 316);
        Node w11 = new Node("w11", 718, 310); Node w12 = new Node("w12", 545, 309);
        Node w13 = new Node("w13", 912, 172); Node w14 = new Node("w14", 773, 176);
        Node w15 = new Node("w15", 538, 180);

        allNodes.addAll(Arrays.asList(eagleD1, eagleD2, whitney, recD1, recD2, centD1, centD2, centD3,
                unionD1, unionD2, unionD3, soccer, vet1, vet2, hetzel, wing,
                w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14, w15));

        // --- Connections ---
        addEdge(w10, unionD2); addEdge(unionD1, unionD2); addEdge(w14, unionD3); addEdge(w12, w2);
        addEdge(soccer, vet1); addEdge(w8, w9); addEdge(w9, w4); addEdge(w8, w10);
        addEdge(w11, unionD1); addEdge(w1, whitney); addEdge(w1, eagleD2);
        addEdge(eagleD1, recD1); addEdge(eagleD1, eagleD2); addEdge(w12, w4);
        addEdge(w11, w9); addEdge(w9, centD3); addEdge(centD2, w6); addEdge(vet1, unionD2);
        addEdge(unionD3, unionD1); addEdge(unionD3, unionD2); addEdge(w10, vet2); addEdge(w8, hetzel);
        addEdge(centD1, centD2); addEdge(centD2, centD3); addEdge(centD3, centD1);
        addEdge(eagleD1, w1); addEdge(whitney, w2); addEdge(recD1, w15); addEdge(recD2, w12);
        addEdge(centD1, w4); addEdge(centD2, w5); addEdge(unionD1, w10); addEdge(unionD2, w13);
        addEdge(soccer, w13); addEdge(vet2, w8); addEdge(wing, w6);
        addEdge(w1, w2); addEdge(w2, w3); addEdge(w3, w4); addEdge(w4, w5);
        addEdge(w5, w6); addEdge(w6, w7); addEdge(w7, w8); addEdge(w10, w11);
        addEdge(w11, w12); addEdge(w12, w15); addEdge(w15, w1); addEdge(w13, w14); addEdge(w14, w15);

        // Campus Shortcuts
        addEdge(w6, w9);
        addEdge(w6, centD3);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (mapImage != null) g2d.drawImage(mapImage, 0, 0, getWidth(), getHeight(), this);

        double scaleX = getWidth() / ORIGINAL_WIDTH;
        double scaleY = getHeight() / ORIGINAL_HEIGHT;

        // --- 1. NETWORK CONNECTIONS ---
        if (techMode) {
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(new Color(0, 255, 255, 150));
        } else {
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{5}, 0));
            g2d.setColor(new Color(255, 255, 255, 30));
        }

        for (Node n : allNodes) {
            for (Edge e : n.adjacencies) {
                g2d.drawLine((int)(n.x * scaleX), (int)(n.y * scaleY),
                        (int)(e.target.x * scaleX), (int)(e.target.y * scaleY));
            }
        }

        // --- 2. THE PATH ---
        if (shortestPath.size() > 1) {
            g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(new Color(255, 50, 50));
            drawAnimatedPath(g2d, scaleX, scaleY);
        }

        // --- 3. NODES & LABELS ---
        for (Node n : allNodes) {
            boolean isWaypoint = n.name.startsWith("w");
            if (isWaypoint && !techMode) continue;

            int sx = (int)(n.x * scaleX), sy = (int)(n.y * scaleY);

            if (isWaypoint) {
                g2d.setColor(Color.YELLOW);
                g2d.fillOval(sx - 3, sy - 3, 6, 6);
                g2d.setColor(Color.CYAN);
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g2d.drawString(n.name, sx + 8, sy + 3);
            } else {
                if (n == startNode) g2d.setColor(Color.GREEN);
                else if (n == endNode) g2d.setColor(Color.RED);
                else g2d.setColor(UWL_MAROON);

                g2d.fillOval(sx - 6, sy - 6, 12, 12);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                g2d.drawString(n.name, sx + 12, sy + 5);
            }
        }

        // --- 4. DEBUG OVERLAY ---
        if (techMode) {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(10, 10, 180, 30);
            g2d.setColor(Color.CYAN);
            g2d.drawString("DEBUG MODE: ACTIVE", 20, 30);
        }
    }

    private void drawAnimatedPath(Graphics2D g2d, double scaleX, double scaleY) {
        int numSegments = shortestPath.size() - 1;
        double currentTarget = animationProgress * numSegments;
        for (int i = 0; i < numSegments; i++) {
            Node u = shortestPath.get(i), v = shortestPath.get(i + 1);
            int x1 = (int)(u.x * scaleX), y1 = (int)(u.y * scaleY);
            int x2 = (int)(v.x * scaleX), y2 = (int)(v.y * scaleY);
            if (i < (int)currentTarget) {
                g2d.drawLine(x1, y1, x2, y2);
            } else if (i == (int)currentTarget) {
                double segProg = currentTarget - i;
                g2d.drawLine(x1, y1, (int)(x1+(x2-x1)*segProg), (int)(y1+(y2-y1)*segProg));
            }
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("UWL Campus Navigator");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new CampusNavigator());
        f.setSize(1400, 900);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}