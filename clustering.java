import java.util.ArrayList;

public class clustering {
    private  ArrayList<ArrayList<BattleNode>> connectedComponents = new ArrayList<ArrayList<BattleNode>>();
    ArrayList<BattleNode> component = new ArrayList<BattleNode>();
    int label;
    int neighbourID;
    boolean labeled;
    boolean n;
    ArrayList<BattleNode> stack = new ArrayList<>();

    clustering(){
        label = 0;
        neighbourID =0;
        labeled = false;
        n = false;
    }
    public void findConnectedComponents(ArrayList<ArrayList<BattleNode>> graph) {

        ArrayList<BattleNode> component = new ArrayList<BattleNode>();
        for (ArrayList<BattleNode> row : graph) {
            for (BattleNode node : row) {
                if (node.hasHit() && node.visited() == false) {
                    stack.add(node);
                    while (stack.isEmpty() == false) {
                        labelNodeAndNeighbours();
                    }
                }
            }
        }
    }

    public void labelNodeAndNeighbours() {
        BattleNode v = stack.get(stack.size()-1);
        stack.remove(stack.size()-1);
        if (v.visited() == false) {
            if (v.getNeighbours() != null) {
                ArrayList<BattleNode> neighbours = v.getNeighbours();
                for (BattleNode neighbour : neighbours) {
                    if (neighbour.visited() == false) {
                        stack.add(neighbour);
                    }
                }

                for (BattleNode neighbour : neighbours) {
                    if (neighbour.compId() != 0) {
                        v.setComponentId(neighbour.compId());
                        v.visit();
                        break;
                    }
                }
                if (v.visited() == false) {
                    if (label > 0) {
                        if(component.size()<5000)
                            connectedComponents.add(component);
                        component = new ArrayList<>();
                    }
                    label = label + 1;
                    v.visit();
                    v.setComponentId(label);
                }
            }
            if(!component.contains(v)) {
                component.add(v);
                v.asMember();
            }
        }
    }

        public ArrayList<ArrayList<BattleNode>> getConnectedComponents(){
        return connectedComponents;
    }

}
