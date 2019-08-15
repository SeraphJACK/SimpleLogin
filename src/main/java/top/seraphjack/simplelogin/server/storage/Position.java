package top.seraphjack.simplelogin.server.storage;

public class Position {
    private final double x, y, z;

    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position cast = (Position) o;
            return x == cast.getX() && y == cast.getY() && z == cast.getZ();
        }
        return false;
    }
}
