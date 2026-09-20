package dev.flanzomc.bbslightfixer.light;

import dev.flanzomc.bbslightfixer.access.CmlBlockAccess;
import dev.lambdaurora.lambdynlights.DynamicLightSource;
import dev.lambdaurora.lambdynlights.LambDynLights;
import mchorse.bbs_mod.forms.forms.BlockForm;
import mchorse.bbs_mod.forms.forms.Form;
import mchorse.bbs_mod.forms.forms.utils.StructureLightSettings;
import mchorse.bbs_mod.forms.renderers.FormRenderingContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class DynamicLightBridge {
 private record Anchor(BlockEntity owner,Matrix4f inverse,Set<Form> seen){}
 private static final Deque<Anchor> ANCHORS=new ArrayDeque<>();
 private static final IdentityHashMap<Object,IdentityHashMap<Form,Source>> SOURCES=new IdentityHashMap<>();
 private static long clock;
 private DynamicLightBridge(){}

 public static void begin(BlockEntity owner,MatrixStack stack){
  ANCHORS.push(new Anchor(owner,new Matrix4f(stack.peek().getPositionMatrix()).invert(),Collections.newSetFromMap(new IdentityHashMap<>())));
 }
 public static void end(){
  if(ANCHORS.isEmpty())return;
  Anchor a=ANCHORS.pop();Map<Form,Source> map=SOURCES.get(a.owner);
  if(map!=null)map.entrySet().removeIf(e->{if(a.seen.contains(e.getKey()))return false;e.getValue().setDynamicLightEnabled(false);return true;});
 }

 public static int luminance(BlockForm form){
  CmlBlockAccess a=(CmlBlockAccess)(Object)form;
  StructureLightSettings s=a.bbsLight$getStructureLight().get();
  boolean enabled=s!=null&&s.enabled;
  int intensity=s==null?15:s.intensity;
  if(!enabled&&a.bbsLight$getEmitLight().get()){enabled=true;intensity=a.bbsLight$getLightIntensity().get();}
  int block=form.blockState.get().getLuminance();
  return enabled&&block>0?Math.max(0,Math.min(15,Math.min(block,intensity))):0;
 }

 public static void capture(Form form,FormRenderingContext context){
  if(!(form instanceof BlockForm block)||luminance(block)<=0)return;
  MinecraftClient client=MinecraftClient.getInstance();if(client.world==null)return;
  Anchor a=ANCHORS.peek();Object owner=a==null?context.entity:a.owner;if(owner==null)return;
  Vector3f p;
  if(a!=null){
   a.seen.add(form);p=new Matrix4f(a.inverse).mul(context.stack.peek().getPositionMatrix()).transformPosition(new Vector3f(.5F,.5F,.5F));
   p.add(a.owner.getPos().getX(),a.owner.getPos().getY(),a.owner.getPos().getZ());
  } else {
   if(context.world==null)return;
   p=context.world.peek().getPositionMatrix().transformPosition(new Vector3f(.5F,.5F,.5F));
  }
  IdentityHashMap<Form,Source> map=SOURCES.computeIfAbsent(owner,k->new IdentityHashMap<>());
  Source s=map.get(form);
  if(s==null){s=new Source(client.world,owner,block);map.put(form,s);s.x=p.x;s.y=p.y;s.z=p.z;s.setDynamicLightEnabled(true);}
  s.x=p.x;s.y=p.y;s.z=p.z;s.seen=clock;
  if(!LambDynLights.get().containsLightSource(s))s.setDynamicLightEnabled(true);
 }

 public static void tick(){
  clock++;World world=MinecraftClient.getInstance().world;
  SOURCES.entrySet().removeIf(entry->{entry.getValue().values().removeIf(s->{
   boolean expired=s.world!=world||(s.owner instanceof BlockEntity b?b.isRemoved()||b.getWorld()!=world:clock-s.seen>2);
   if(expired)s.setDynamicLightEnabled(false);return expired;
  });return entry.getValue().isEmpty();});
 }

 private static final class Source implements DynamicLightSource{
  final World world;final Object owner;final BlockForm form;double x,y,z,lastX=Double.NaN,lastY,lastZ;long seen;int lastLevel=-1;final Set<BlockPos> sections=new HashSet<>();
  Source(World world,Object owner,BlockForm form){this.world=world;this.owner=owner;this.form=form;}
  public double getDynamicLightX(){return x;} public double getDynamicLightY(){return y;} public double getDynamicLightZ(){return z;}
  public World getDynamicLightWorld(){return world;} public int getLuminance(){return DynamicLightBridge.luminance(form);}
  public void resetDynamicLight(){lastLevel=-1;} public void dynamicLightTick(){} public boolean shouldUpdateDynamicLight(){return true;}
  public boolean lambdynlights$updateDynamicLight(WorldRenderer renderer){
   int level=getLuminance();if(level==lastLevel&&Math.abs(x-lastX)<.1&&Math.abs(y-lastY)<.1&&Math.abs(z-lastZ)<.1)return false;
   Set<BlockPos> next=new HashSet<>();
   if(level>0)for(int sx=(int)Math.floor((x-8)/16);sx<=(int)Math.floor((x+8)/16);sx++)for(int sy=(int)Math.floor((y-8)/16);sy<=(int)Math.floor((y+8)/16);sy++)for(int sz=(int)Math.floor((z-8)/16);sz<=(int)Math.floor((z+8)/16);sz++)next.add(new BlockPos(sx,sy,sz));
   sections.addAll(next);lambdynlights$scheduleTrackedChunksRebuild(renderer);sections.clear();sections.addAll(next);
   lastX=x;lastY=y;lastZ=z;lastLevel=level;return true;
  }
  public void lambdynlights$scheduleTrackedChunksRebuild(WorldRenderer renderer){for(BlockPos p:sections)LambDynLights.scheduleChunkRebuild(renderer,p.getX(),p.getY(),p.getZ());}
 }
}
