package portals;

import joml.Vector3f;

public class Seam {

    private Portal m_local;
    private Portal m_remote;

    public Seam(Portal local, Portal remote) {
        m_local = local;
        m_remote = remote;
    }

    public Vector3f transformPoint(Vector3f original) {
        Vector3f delta = original.get().sub(m_local.getBasePosition());
        delta = Basis.change(delta, Basis.STANDARD, m_local.getFrontBasis());
        delta = Basis.change(delta, m_remote.getBackBasis(), Basis.STANDARD);
        return m_remote.getBasePosition().add(delta);
    }

    public Vector3f transformVector(Vector3f original) {
        Vector3f vector = original.get();
        vector = Basis.change(vector, Basis.STANDARD, m_local.getFrontBasis());
        vector = Basis.change(vector, m_remote.getBackBasis(), Basis.STANDARD);
        return vector;
    }
}