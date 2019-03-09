// Description: A Ship that can be controlled by a Player
// Created: 3/2/19

package Player;

import Physics.*;
import Physics.MoveableRectangle;
import Physics.MoveableShape;
import Weapon.*;
import Helpers.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Ship implements Drawable, Moveable {

    private Vector position, velocity, acceleration;
    private Image gif;
    private BufferedImage bufferedImage;
    private int health;
    private Weapon weapon;
    private int rotation;
    private final int initalRotation = 90; // ship starts off facing up
    private Dimension screenSize;
    private ShipType shipType;

    public static final int MOVEMENT_SPEED = 2;
    public static final int TURNING_SPEED = 1;

    public enum ShipType {
        RED("graphics/Arwing-Red.gif", new MoveableRectangle(new Vector(97, 120), 20, 40), new MoveableRectangle(new Vector(132, 140), 90, 20)),
        GREEN("graphics/Arwing-Green.gif", new MoveableRectangle(new Vector(90, 120), 15, 40), new MoveableRectangle(new Vector(124, 130), 82, 20)),
        BLUE("graphics/Arwing-Blue.gif", new MoveableRectangle(new Vector(105, 120), 20, 40), new MoveableRectangle(new Vector(145, 140), 100, 20));

        private String iconAddress;
        private Hitbox hitbox, originalHitbox;

        ShipType(String iconAddress, MoveableShape rect1, MoveableShape rect2) {
            this.iconAddress = iconAddress;
            this.hitbox = new Hitbox();
            hitbox.add(rect1);
            hitbox.add(rect2);
            this.originalHitbox = new Hitbox();
            originalHitbox.add(new MoveableRectangle((MoveableRectangle) rect1));
            originalHitbox.add(new MoveableRectangle((MoveableRectangle) rect2));
        }
    }

    public Ship() {
        this(new Dimension(1572, 912));
    }

    public Ship(Dimension screenSize) {
        this(ShipType.BLUE, screenSize);
    }

    public Ship (ShipType shipType, Dimension screenSize) {
        this(new Vector(100, 100), new Vector(0, 0), new Vector(0, 0), new Weapon(new Laser(new Vector(), new Vector())), shipType, screenSize);
        updateCenter();
    }

    public Ship(Vector position, Vector velocity, Vector acceleration, Weapon weapon, ShipType shipType, Dimension screensize) {
        this.position = position;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.weapon = weapon;
        rotation = initalRotation;
        gif = new ImageIcon(shipType.iconAddress).getImage();
        try {
            this.bufferedImage = ImageIO.read(new File(shipType.iconAddress));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        this.shipType = shipType;
        this.screenSize = screensize;
    }

    public void draw(Graphics g) {
        g.drawImage(Helpers.rotateImage(gif, Math.toRadians(-rotation + initalRotation)), (int) position.getX(), (int) position.getY(), null);

        //shipType.hitbox.draw(g);
    }

    public void move() {
        Vector tempPosition = new Vector(position);
        tempPosition.add(velocity);

        if (tempPosition.getX() >= 0 && tempPosition.getX() <= screenSize.width - bufferedImage.getWidth())
            position.add(new Vector(velocity.getX(), 0));
        if (tempPosition.getY() >= 0 && tempPosition.getY() <= screenSize.height - bufferedImage.getHeight())
            position.add(new Vector(0, velocity.getY()));

        velocity.add(acceleration);
        for (int index = 0; index < shipType.hitbox.size(); index++) {
            MoveableRectangle originalRect = (MoveableRectangle) shipType.originalHitbox.get(index);
            Vector relativePosition = new Vector(originalRect.getPosition());
            relativePosition.add(position);
            relativePosition.add(new Vector(-originalRect.getWidth() / 2.0, -originalRect.getHeight() / 2.0)); // Move position to be relative to the center of the Ship
            relativePosition.add(new Vector(-1 * bufferedImage.getWidth() / 2.0, -1 * bufferedImage.getHeight() / 2.0)); // Move position to be relative to the top left corner of the Ship
            shipType.hitbox.get(index).setPosition(relativePosition);
        }

        velocity = new Vector(0, 0);

        shipType.hitbox.setRotation(-rotation + initalRotation);
        updateCenter();
    }

    public Projectile fire() {
        return weapon.fire(new Vector(position.getX() + bufferedImage.getWidth() / 2, position.getY() + bufferedImage.getHeight() / 2), Math.toRadians(-rotation));
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public void turn(int degCCW) {
        rotation -= degCCW;
    }

    public int getRotation() {
        return rotation;
    }

    private void updateCenter() {
        shipType.hitbox.setCenter(new Vector(position.getX() + bufferedImage.getWidth() / 2, position.getY() + bufferedImage.getHeight() / 2));
    }

    // Getters and Setters
    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void addVelocity(Vector velocity) {
        this.velocity.add(velocity);
    }

    public Vector getAcceleration() {
        return acceleration;
    }

    public void addAcceleration(Vector acceleration) {
        this.acceleration.add(acceleration);
    }

}
