package world;

import rendering.Camera;
import rendering.RenderingException;

public class World {

    private Module m_currentModule;
    private Camera m_camera;

    public World() {
        m_camera = new Camera();
        m_currentModule = Treadmill.getInitialModule();
        //Treadmill.buildAround(m_currentModule, new Ray());
    }

    public void update(float seconds) {
//        Treadmill.buildAround(m_currentModule, new Ray());
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            String command = br.readLine();
//            parseTypedCommand(command);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void render() {
        m_currentModule.stageScene();
        try {
            m_camera.capture(m_currentModule);
        } catch (RenderingException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private void parseTypedCommand(String command) {
        String[] tokenized = command.split("\\s+");
        switch (tokenized[0]) {
        case "move":
            String where = tokenized[1];
            for (Portal portal : m_currentModule.getTemplate().getPortals()) {
                if (portal.getName().equalsIgnoreCase(where)) {
                    if (m_currentModule.hasNeighbor(portal)) {
                        m_currentModule = m_currentModule.getNeighbor(portal);
                        return;
                    } else {
                        System.out.println("Portal " + where + " has no loaded neighbor.");
                        return;
                    }
                }
            }
            System.out.println("Couldn't find exit " + where);
            break;
        default:
            System.out.println("Unknown command");
        }
    }
}
