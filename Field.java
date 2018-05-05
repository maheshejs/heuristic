package heuristic;
import static heuristic.Constants.*;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Field extends Group{
    static Set<Point2D> allPoints = new HashSet<>();
    static Set<Point2D> forbPoints = new HashSet<>();
    static INode startNode = new INode(); 
    static INode endNode = new INode();
    Field(){
        super(); 
        double coord_x = START_X, coord_y = START_Y;
        for(int i = 0 ;i<SIZE; i++){
            coord_x = START_X;
            for(int j=0; j<SIZE;j++){
                GBox gBox = new GBox(coord_x, coord_y);
                for(double[] coord : gBox.getCoords())
                    allPoints.add(new Point2D(coord[0], coord[1]));
                this.getChildren().add(gBox);
                coord_x+=WIDTH;
            }
            coord_y+=HEIGHT;
        }
    }
    private class GBox extends Rectangle{
        private double[][] coords = new double[4][2];
        private GBox(double x, double y){
            super(x, y, WIDTH, HEIGHT);
            this.setStroke(Color.BLUE);
            this.setFill(Color.WHITE);
            this.setOnMouseClicked(e -> action (e));
        }
        private void setCoords(){
            for(int i=0; i<4;i++){
                this.coords[i][0]= this.getX() + (int)(i/2) * WIDTH;
                this.coords[i][1]= this.getY() + (i%2) * HEIGHT;
            }
        }
        private double[][] getCoords(){
            setCoords();
            return this.coords;
        }
        private void action (MouseEvent e){
            for(double[] coord : this.getCoords()){
                double x = coord[0];
                double y = coord[1];
                boolean testX = Math.abs(x-e.getX())<=FACT_RED;
                boolean testY = Math.abs(y-e.getY())<=FACT_RED;
                Point2D point = new Point2D(x, y);
                if (testX && testY){
                    Circle c = new Circle(point.getX(), point.getY(), RADIUS);
                    if(e.getButton().equals(MouseButton.PRIMARY)){
                        if(Field.startNode.equals(new INode())){
                            Field.startNode = new INode(point);
                            c.setFill(COLORS[0]);
                        }
                        else{
                            forbPoints.add(point);
                        }
                    }
                    else if(e.getButton().equals(MouseButton.SECONDARY)){
                        if(Field.endNode.equals(new INode())){
                            Field.endNode = new INode(point);
                            c.setFill(COLORS[1]);
                        }
                        else
                            break;
                    }
                    Field.this.getChildren().add(c); 
                    break;
                }
            }
        }
    }
}