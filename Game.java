package heuristic;
import static heuristic.Constants.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Game extends Application{
    private Comparator<INode> comparator = Comparator.comparing(INode::getCost, Comparator.naturalOrder());
    private Queue<INode> open = new PriorityQueue<>(comparator);
    private Set<INode> closed = new HashSet<>();
    private Field field = new Field(); 
    private Set<Point2D> allPoints = Field.allPoints;
    private Set<Point2D> forbPoints = Field.forbPoints;
    private INode startNode = new INode();
    private INode endNode = new INode();
    @Override
    public void start(Stage stage){
        VBox vBox = new VBox();
        Button btn = new Button("Find path");
        btn.setOnAction(e -> findPath());
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(25, 25, 25, 25));
        vBox.getChildren().addAll(field, btn);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.setTitle("Shortest path");
        stage.show();
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
    public void findPath(){
        startNode = Field.startNode;
        endNode = Field.endNode;
        startNode.setCost(startNode.distance(endNode));
        open.offer(startNode);
        while(true){
            if(open.isEmpty()){
                System.out.println("No path found");
                break; //failure
            }
            INode leafNode = open.poll();
                    
            Circle circle = new Circle(leafNode.getX(), leafNode.getY(), RADIUS);
            circle.setFill(COLORS[3]);
            field.getChildren().add(circle);
            
            if(leafNode.equals(endNode)){
                List<Point2D> parcours = new ArrayList<>();
                INode parentLeafNode = leafNode.getParent();
                while(parentLeafNode!=null){
                    if(parentLeafNode.getParent()!=null)
                        parcours.add(parentLeafNode);
                    parentLeafNode = parentLeafNode.getParent();
                }
                for(Point2D point : parcours){
                    Circle c = new Circle(point.getX(), point.getY(), RADIUS);
                    c.setFill(COLORS[2]);
                    field.getChildren().add(c);
                }
                
                Circle c1 = new Circle(startNode.getX(), startNode.getY(), RADIUS);
                Circle c2 = new Circle(endNode.getX(), endNode.getY(), RADIUS);
                c1.setFill(COLORS[0]);
                c2.setFill(COLORS[1]);
                field.getChildren().addAll(c1,c2);

                break; //success
            }
            closed.add(leafNode);
            List<INode> childrenNode = alentour(leafNode);
            for(INode childNode : childrenNode){
                if(!(closed.contains(childNode) || open.contains(childNode))){
                    open.offer(childNode);
                }
                else if (open.contains(childNode)){
                    for(INode iterNode : open){
                        if(iterNode.equals(childNode) && iterNode.getCost()>childNode.getCost()){
                            open.remove(iterNode);
                            open.offer(childNode);
                            break;
                        }
                    }
                }
            }
        }
    }
    public List<INode> alentour(INode iNode){ 
        List<INode> alentour=new ArrayList<>();
        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                double x = iNode.getX()+(i-1)*WIDTH;
                double y = iNode.getY()+(j-1)*HEIGHT;
                Point2D sidePoint = new Point2D(x, y);
                if(i==1 && j==1)
                    continue;
                if(allPoints.contains(sidePoint) && !(forbPoints.contains(sidePoint))){
                    INode elemNode = new INode(sidePoint);
                    //double stepCost = elemNode.distance(endNode) + 1;
                    elemNode.setParent(iNode);
                    double parentCost = elemNode.getParent().getCost();
                    double heurCost = elemNode.distance(endNode);
                    double stepCost = elemNode.distance(elemNode.getParent());
                    double cost = parentCost + heurCost + stepCost;

                    //if(elemNode.getParent()==null)
                    //    elemNode.setCost(startNode.getCost() + stepCost);
                    //else
                        //elemNode.setCost(elemNode.getParent().getCost() + stepCost);
                    elemNode.setCost(cost);
                        //elemNode.setCost(elemNode.getParent().getCost()+elemNode.distance(endNode)+1);
                    alentour.add(elemNode);
                }
            }
        }
        return alentour;
    }
}