import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * VectorSnake: a small Java Swing game demonstrating grid movement,
 * direction vectors, wall/self collision, food spawning, and frame timing.
 */
public class VectorSnake extends JPanel implements ActionListener, KeyListener {
    private static final int CELL = 24;
    private static final int COLS = 25;
    private static final int ROWS = 22;
    private static final int HUD = 42;
    private static final int WIDTH = COLS * CELL;
    private static final int HEIGHT = ROWS * CELL + HUD;

    private final Deque<Point> snake = new ArrayDeque<>();
    private final Random random = new Random();
    private final Timer timer = new Timer(100, this);

    // Direction is a 2D vector on the grid.
    private int dx = 1;
    private int dy = 0;
    private int nextDx = 1;
    private int nextDy = 0;
    private Point food;
    private int score = 0;
    private boolean gameOver = false;
    private boolean started = false;

    public VectorSnake() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(15, 23, 42));
        setFocusable(true);
        addKeyListener(this);
        resetGame();
        timer.start();
    }

    private void resetGame() {
        snake.clear();
        int x = COLS / 2;
        int y = ROWS / 2;
        snake.addFirst(new Point(x, y));
        snake.addLast(new Point(x - 1, y));
        snake.addLast(new Point(x - 2, y));
        dx = nextDx = 1;
        dy = nextDy = 0;
        score = 0;
        gameOver = false;
        started = false;
        spawnFood();
        repaint();
    }

    private void spawnFood() {
        Point candidate;
        do {
            candidate = new Point(random.nextInt(COLS), random.nextInt(ROWS));
        } while (snake.contains(candidate));
        food = candidate;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (started && !gameOver) {
            updateGame();
        }
        repaint();
    }

    private void updateGame() {
        dx = nextDx;
        dy = nextDy;

        Point head = snake.peekFirst();
        Point newHead = new Point(head.x + dx, head.y + dy);

        // Wall collision.
        if (newHead.x < 0 || newHead.x >= COLS ||
            newHead.y < 0 || newHead.y >= ROWS) {
            gameOver = true;
            return;
        }

        boolean eating = newHead.equals(food);

        // If not eating, the tail moves away this tick, so stepping into
        // the current tail cell is legal. Check the other body cells.
        int checked = 0;
        int bodySize = snake.size();
        for (Point segment : snake) {
            if (!eating && checked == bodySize - 1) {
                break; // current tail vacates its cell
            }
            if (segment.equals(newHead)) {
                gameOver = true;
                return;
            }
            checked++;
        }

        snake.addFirst(newHead);
        if (eating) {
            score += 10;
            if (snake.size() == COLS * ROWS) {
                gameOver = true;
                return;
            }
            spawnFood();
        } else {
            snake.removeLast();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(30, 41, 59));
        g2.fillRect(0, 0, WIDTH, HUD);
        g2.setFont(new Font("SansSerif", Font.BOLD, 17));
        g2.setColor(new Color(226, 232, 240));
        g2.drawString("VECTOR SNAKE", 14, 27);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g2.drawString("Score: " + score, WIDTH - 100, 27);

        // Subtle grid helps make the movement and collision geometry visible.
        g2.setColor(new Color(30, 41, 59));
        for (int x = 0; x <= WIDTH; x += CELL) {
            g2.drawLine(x, HUD, x, HEIGHT);
        }
        for (int y = HUD; y <= HEIGHT; y += CELL) {
            g2.drawLine(0, y, WIDTH, y);
        }

        drawCell(g2, food, new Color(251, 113, 133));
        boolean isHead = true;
        for (Point segment : snake) {
            drawCell(g2, segment, isHead
                    ? new Color(45, 212, 191)
                    : new Color(16, 185, 129));
            isHead = false;
        }

        if (!started || gameOver) {
            g2.setColor(new Color(2, 6, 23, 205));
            g2.fillRoundRect(45, HEIGHT / 2 - 58, WIDTH - 90, 116, 18, 18);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            String title = gameOver ? "Game Over" : "Ready to play?";
            drawCentered(g2, title, HEIGHT / 2 - 15);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
            String instruction = gameOver
                    ? "Press SPACE to restart"
                    : "Arrow keys / WASD to move • Press any direction to start";
            drawCentered(g2, instruction, HEIGHT / 2 + 17);
        }
        g2.dispose();
    }

    private void drawCell(Graphics2D g2, Point p, Color color) {
        g2.setColor(color);
        g2.fillRoundRect(p.x * CELL + 2, HUD + p.y * CELL + 2,
                         CELL - 4, CELL - 4, 7, 7);
    }

    private void drawCentered(Graphics2D g2, String text, int y) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (WIDTH - fm.stringWidth(text)) / 2, y);
    }

    private void setDirection(int newDx, int newDy) {
        // Prevent a 180-degree reversal into the snake's neck.
        if (newDx == -dx && newDy == -dy) return;
        nextDx = newDx;
        nextDy = newDy;
        started = true;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE && gameOver) {
            resetGame();
            return;
        }
        switch (key) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W: setDirection(0, -1); break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S: setDirection(0, 1); break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A: setDirection(-1, 0); break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: setDirection(1, 0); break;
            default: break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("VectorSnake — Java 2D Game");
            VectorSnake game = new VectorSnake();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.setContentPane(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            game.requestFocusInWindow();
        });
    }
}
