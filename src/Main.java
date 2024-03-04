import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File file = new File(args[0]);
        FileWriter output = new FileWriter(args[1]);
        output.write("");
        Scanner scan = new Scanner(file);
        MemberTree members = new MemberTree();

        // inserting root to the avl tree
        String boss = scan.nextLine();
        String[] bossNameGms = boss.split(" ");
        members.insert(bossNameGms[0],Double.parseDouble(bossNameGms[1]));
        //reading the text
        while (scan.hasNextLine()){
            String line = scan.nextLine();
            String[] info = line.split(" ");
            if (info[0].equals("MEMBER_IN")){
                String name = info[1];
                double gms = Double.parseDouble(info[2]);
                // find the superiors who welcome the newcomer
                ArrayList<String> welcomes = findRoute(gms,members.root,new ArrayList<>());
                members.insert(name,gms);

                for (int i = 0; i< welcomes.size();i++)
                    output.append(welcomes.get(i)+" welcomed " + name+"\n");
            }
            else if (info[0].equals("MEMBER_OUT")){
                String name = info[1];
                double gms = Double.parseDouble(info[2]);
                MemberTree.Node leavingNode = members.find(gms,members.root);
                // if it has no successors it isn't replaced
                if (leavingNode.right == null && leavingNode.left == null)
                    output.append(name + " left the family, replaced by nobody\n");

                // if it has only one successor then it is replaced by it
                else if (leavingNode.right == null)
                    output.append(name + " left the family, replaced by " + leavingNode.left.name+"\n");
                else if (leavingNode.left == null)
                    output.append(name + " left the family, replaced by " + leavingNode.right.name+"\n");
                // if it has two successors it is replaced by the lowest higher value
                else {
                    String replaced = members.findMin(leavingNode.right).name;
                    output.append(name + " left the family, replaced by " + replaced+"\n");
                }
                members.remove(gms);

            }
            else if (info[0].equals("INTEL_TARGET")){
                // find the common superior and it's gms
                MemberTree.Node node1 = members.find(Double.parseDouble(info[2]),members.root);
                MemberTree.Node node2 = members.find(Double.parseDouble(info[4]),members.root);
                MemberTree.Node superior = MemberTree.lowestSuperior(node1,node2,members.root);
                output.append("Target Analysis Result: " + superior.name + " " + String.format("%.3f",superior.gms)+"\n");

            }
            else if (info[0].equals("INTEL_DIVIDE")){
                int sum = 0;
                while (members.root.activity == 1){
                    sum += MemberTree.intelDivide(members.root);
                }
                output.append("Division Analysis Result: " + sum+"\n");
                MemberTree.restoreActivity(members.root);

            }
            else if (info[0].equals("INTEL_RANK")){
                double gms = Double.parseDouble(info[2]);
                int rank = MemberTree.findRank(gms,members.root,0);
                ArrayList<MemberTree.Node> rankList = new ArrayList<>();
                // finds the nodes with the same rank
                MemberTree.rankList(members.root,rankList,rank);
                output.append("Rank Analysis Result:");
                for (MemberTree.Node x : rankList)
                    output.append(" " + x.name + " " + String.format("%.3f",x.gms));
                output.append("\n");
            }
        }
        output.close();
    }
    // start from the root and add them to an array until it reaches the inserted node
    private static ArrayList<String> findRoute(double gms,MemberTree.Node t,ArrayList<String> route){
        if (t == null)
            return route;
        route.add(t.name);
        if (gms < t.gms)
            return findRoute(gms,t.left,route);
        if (gms>t.gms)
            return findRoute(gms,t.right,route);
        return new ArrayList<>();
    }

}