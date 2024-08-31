import bpy
import re
import threading
import time
import logging

logging.basicConfig(level=logging.DEBUG,format='(%(threadName)-10s) %(message)s',)
logging.debug("Running Script!")

export_mesh = True
export_animation = False
obj_original_name = ""
armature_original_name = ""
mesh_original_name = ""
name_conflict = False
filepath = "C://Users//micha//Documents//AI//Channel Cat//2024_08_23//Blender//test_script//"
filename = ""

def fbx_export(filepath, export_anim_bool):
    filepath = filepath+filename
    bpy.ops.export_scene.fbx(
        filepath=filepath,
        check_existing=True,
        filter_glob='*.fbx',
        use_selection=True,
        use_visible=False,
        use_active_collection=False,
        global_scale=1.0,
        apply_unit_scale=True,
        apply_scale_options='FBX_SCALE_UNITS',
        use_space_transform=True,
        bake_space_transform=True,
        object_types={'EMPTY', 'CAMERA', 'LIGHT', 'ARMATURE', 'MESH', 'OTHER'},
        use_mesh_modifiers=False,
        mesh_smooth_type='FACE',
        colors_type='SRGB',
        prioritize_active_color=False,
        use_subsurf=False,
        use_mesh_edges=False,
        use_tspace=False,
        use_triangles=False,
        use_custom_props=False,
        add_leaf_bones=False,
        primary_bone_axis='Y',
        secondary_bone_axis='X',
        use_armature_deform_only=False,
        armature_nodetype='NULL',
        bake_anim=export_anim_bool,
        bake_anim_use_all_bones=True,
        bake_anim_use_nla_strips=False,
        bake_anim_use_all_actions=False,
        bake_anim_force_startend_keying=True,
        bake_anim_step=1.0,
        bake_anim_simplify_factor=1.0,
        path_mode='AUTO',
        batch_mode='OFF',
        use_batch_own_dir=False,
        use_metadata=True,
        axis_forward='-Z',
        axis_up='Y'
    )

# Function to clean up the name by removing the number and extra period
def clean_object_name(name):
    return re.sub(r'\.\d+\.', '.', name)

# Get the selected object
selected_object = bpy.context.active_object
if selected_object.type == "ARMATURE":
    # Clean the name of the selected object
    cleaned_armature_name = clean_object_name(selected_object.name)
    
    # Check if any other objects already have the cleaned name
    for obj in bpy.data.objects:
        if obj != selected_object and obj.name == cleaned_armature_name:
            
            name_conflict = True
            
            logging.debug("Entered cleaned name is same as object name")
            
            # Rename the other object to avoid collision
            obj_original_name = obj.name
            armature_original_name = bpy.data.armatures[obj.name].name
            
            # Rename the conflicting Armature
            bpy.data.armatures[obj.name].name = bpy.data.armatures[obj.name].name + "_temp"
            
            for child in obj.children:
                if child.type == 'MESH':
                    mesh_original_name = child.name
                    # Rename the Mesh
                    child.name = child.name + "_temp"
            
            # Rename the conflicting Armature Object
            obj.name = obj.name + "_temp"
            
            #logging.debug(bpy.data.armatures[obj.name].name)
    
    bpy.data.armatures[selected_object.name].name = cleaned_armature_name
    
    # Now safely rename the selected object
    selected_object.name = cleaned_armature_name
    
    for child in selected_object.children:
        if child.type == 'MESH':
            child.name = clean_object_name(child.name)
            #bpy.ops.object.select_all(action='DESELECT')
            child.select_set(True)

            #logging.debug(selected_object.pose.bones[0].name)
            
if export_mesh:
    filename = selected_object.name.split('.')[0] + "_mesh.fbx"
    fbx_export(filepath, False)
if export_animation:
    #need to check the file path to see if there's already animation, if so, we should iterate to the next number, starting with 00001
    filename = selected_object.name.split('.')[0] + "_animation.fbx"    
    fbx_export(filepath, True)

if name_conflict:
    #change the names of the files back to their original names
    logging.debug("change filenames back to OG names")
    