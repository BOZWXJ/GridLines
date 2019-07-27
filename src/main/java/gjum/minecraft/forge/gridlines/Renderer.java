package gjum.minecraft.forge.gridlines;

import gjum.minecraft.forge.gridlines.config.GridLinesConfig;
import gjum.minecraft.forge.gridlines.config.GridPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private Minecraft mc = Minecraft.getMinecraft();

    public Renderer() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private long lastCrash = 0;

    @SubscribeEvent
    public void RenderWorldLastEvent(RenderWorldLastEvent event) {
        try {
            GridLinesConfig conf = GridLinesConfig.instance;
            if (!conf.enabled) return;

            EntityPlayerSP p = Minecraft.getMinecraft().player;
            if (p == null) return;
            float partialTicks = event.getPartialTicks();
            double x = p.lastTickPosX + (p.posX - p.lastTickPosX) * partialTicks;
            double y = p.lastTickPosY + (p.posY - p.lastTickPosY) * partialTicks;
            double z = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * partialTicks;

            GL11.glPushMatrix();
            glTranslated(-x, -y, -z);
            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            if (!conf.depthTest) glDisable(GL_DEPTH_TEST);

            if (conf.grid1Enabled) {
                Color color;
                try {
                    color = Color.decode(conf.grid1Color);
                } catch (NumberFormatException e) {
                    color = Color.WHITE;
                }
                if (conf.grid1GridPattern == GridPattern.SQUARE) {
                    renderSquareGrid(x, y, z, conf.grid1Interval, conf.grid1XAnchor, conf.grid1ZAnchor, conf.renderDistance, color);
                } else if (conf.grid1GridPattern == GridPattern.DIAMOND){
                    renderDiamondGrid(x, y, z, conf.grid1Interval, conf.grid1XAnchor, conf.grid1ZAnchor, conf.renderDistance, color);
                } else {
                    // TODO other grid types
                }
            }

            if (conf.grid2Enabled) {
                Color color;
                try {
                    color = Color.decode(conf.grid2Color);
                } catch (NumberFormatException e) {
                    color = Color.WHITE;
                }
                if (conf.grid2GridPattern == GridPattern.SQUARE) {
                    renderSquareGrid(x, y, z, conf.grid2Interval, conf.grid2XAnchor, conf.grid2ZAnchor, conf.renderDistance, color);
                } else if (conf.grid2GridPattern == GridPattern.DIAMOND){
                    renderDiamondGrid(x, y, z, conf.grid2Interval, conf.grid2XAnchor, conf.grid2ZAnchor, conf.renderDistance, color);
                } else {
                    // TODO other grid types
                }
            }

            if (!conf.depthTest) glEnable(GL_DEPTH_TEST);
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            GL11.glPopMatrix();

        } catch (Exception e) {
            if (lastCrash < System.currentTimeMillis() - 5000) {
                lastCrash = System.currentTimeMillis();
                e.printStackTrace();
            }
        }
    }

    private void renderSquareGrid(double px, double py, double pz, int interval, int xAnchor, int zAnchor, int renderDistance, Color color) {
        int pxi = (int) Math.floor(px);
        int pzi = (int) Math.floor(pz);
        int west = nearestSquareGridAnchor(pxi - renderDistance, xAnchor, interval);
        int north = nearestSquareGridAnchor(pzi - renderDistance, zAnchor, interval);

        int east = west + 2 * renderDistance;
        int south = north + 2 * renderDistance;

        for (int gx = west; gx <= east; gx += interval) {
            for (int gz = north; gz <= south; gz += interval) {
                drawVerticalLine(gx + .5f, gz + .5f, .1f, 0, 256,
                        color.getRed(), color.getGreen(), color.getBlue(), 100);
            }
        }
    }

    private void renderDiamondGrid(double px, double py, double pz, int interval, int xAnchor, int zAnchor, int renderDistance, Color color){
        int pxi = (int) Math.floor(px);
        int pzi = (int) Math.floor(pz);
        int west = nearestSquareGridAnchor(pxi - renderDistance, xAnchor, interval);
        int north = nearestSquareGridAnchor(pzi - renderDistance, zAnchor, interval);

        int east = west + 2 * renderDistance;
        int south = north + 2 * renderDistance;

        boolean xf = Math.abs(west - xAnchor) % (interval * 2) == 0;
        boolean zf = Math.abs(north - zAnchor) % (interval * 2) == 0;
        for (int gx = west; gx <= east; gx += interval) {
            for (int gz = ( xf == zf ? north : north + interval); gz <= south; gz += interval * 2) {
                    drawVerticalLine(gx + .5f, gz + .5f, .1f, 0, 256,
                            color.getRed(), color.getGreen(), color.getBlue(), 100);
            }
            xf = !xf;
        }
    }

    int nearestSquareGridAnchor(int coord, int anchor, int interval) {
        int off = (coord - anchor) % interval;
        if (off < 0) off = interval + off;
        return coord - off;
    }

    public static void drawVerticalLine(float cx, float cz, float radius, float yMin, float yMax, int red, int green, int blue, int alpha) {
        float west = cx - radius;
        float east = cx + radius;
        float north = cz - radius;
        float south = cz + radius;

        glColor4ub((byte) red, (byte) green, (byte) blue, (byte) alpha);
        glBegin(GL_TRIANGLE_STRIP);

        glVertex3f(west, yMax, north);
        glVertex3f(west, yMin, north);
        glVertex3f(west, yMax, south);
        glVertex3f(west, yMin, south);
        glVertex3f(east, yMax, south);
        glVertex3f(east, yMin, south);
        glVertex3f(east, yMax, north);
        glVertex3f(east, yMin, north);
        glVertex3f(west, yMax, north);
        glVertex3f(west, yMin, north);

        glEnd();
    }

}
