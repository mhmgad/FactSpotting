package entitydisambiguation;

/**
 * Created by gadelrab on 11/2/16.
 */
public class Entity {


    private String id;
    private double score;

    public Entity(String id, double score) {
        this.id=id;
        this.score=score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "id='" + id + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
