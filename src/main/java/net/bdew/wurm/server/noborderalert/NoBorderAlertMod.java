package net.bdew.wurm.server.noborderalert;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NoBorderAlertMod implements WurmServerMod, Initable, PreInitable {
    private static final Logger logger = Logger.getLogger("NoBorderAlertMod");

    private static void logInfo(String msg) {
        if (logger != null)
            logger.log(Level.INFO, msg);
    }

    @Override
    public void init() {
        try {
            ClassPool classPool = HookManager.getInstance().getClassPool();
            classPool.getCtClass("com.wurmonline.server.creatures.Communicator")
                    .getMethod("checkLegalTileMove", "(IIII)Z")
                    .instrument(new ExprEditor() {
                        private boolean stop = false;
                        @Override
                        public void edit(MethodCall m) throws CannotCompileException {
                            if (!stop) {
                                if (m.getMethodName().equals("sendAlertServerMessage") || m.getMethodName().equals("sendPopup")) {
                                    logInfo(String.format("Removing call to %s at %d", m.getMethodName(), m.getLineNumber()));
                                    m.replace("");
                                } else if (m.getMethodName().equals("isDead")) {
                                    stop = true;
                                    logInfo(String.format("Stopping at %d", m.getLineNumber()));
                                }
                            }
                        }
                    });
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void preInit() {
    }
}
