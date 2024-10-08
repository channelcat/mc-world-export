# fbx_quick_export.py

bl_info = {
    "name": "FBX Quick Export",
    "author": "Mikey Guggenheim",
    "version": (1, 0),
    "blender": (2, 80, 0),
    "location": "View3D > Sidebar > FBX Quick Export",
    "description": "Quickly export FBX files with custom settings",
    "warning": "",
    "doc_url": "",
    "category": "Import-Export",
}

import bpy
import re
import logging
import os
from bpy.props import StringProperty, BoolProperty, PointerProperty
from bpy.types import PropertyGroup, Operator, Panel
from bpy.app.handlers import persistent

logging.basicConfig(level=logging.DEBUG, format='(%(threadName)-10s) %(message)s')
logging.debug("FBX Quick Export: Script is running")

def toggle_export_options(self, context):
    props = context.scene.fbx_export_props
    if not props.export_mesh and not props.export_animation:
        # If both are unchecked, set the one that was just unchecked back to checked
        if self == props.export_mesh:
            props.export_animation = True
        else:
            props.export_mesh = True

class FBXExportProperties(PropertyGroup):
    filepath: StringProperty(
        name="Export Path",
        description="Path to export FBX files",
        default="",
        maxlen=1024,
        subtype='DIR_PATH'
    )
    filename: StringProperty(
        name="File Name Prefix",
        description="Base name of the exported file (without extension)",
        default="",
        maxlen=255
    )
    preview_filename: StringProperty(
    name="Preview Filename",
    description="Preview of the generated filename",
    default="",
    )
    export_mesh: BoolProperty(
        name="Export Mesh",
        description="Export mesh FBX",
        default=True,
        update=toggle_export_options
    )
    export_animation: BoolProperty(
        name="Export Animation",
        description="Export animation FBX",
        default=False,
        update=toggle_export_options
    )

@persistent
def update_filename_on_object_select(dummy):
    active_object = bpy.context.active_object
    if active_object:
        props = bpy.context.scene.fbx_export_props
        if active_object.type == "ARMATURE":
            object_name = active_object.name.split('.')[0]
            prefix = props.filename if props.filename else ""
            preview_lines = []

            if props.export_mesh:
                preview_mesh = f"{prefix}_{object_name}_mesh.fbx" if prefix else f"{object_name}_mesh.fbx"
                preview_lines.append(f"Mesh: {preview_mesh}")

            if props.export_animation:
                base_filename = f"{prefix}_{object_name}_animation" if prefix else f"{object_name}_animation"
                animation_index = 1
                while True:
                    preview_anim = f"{base_filename}_{animation_index:02d}.fbx"
                    if not os.path.exists(os.path.join(props.filepath, preview_anim)):
                        break
                    animation_index += 1
                preview_lines.append(f"Animation: {preview_anim}")

            props.preview_filename = "\n".join(preview_lines)
        else:
            props.preview_filename = "Please select an armature!"

def make_clickable_link(self, file_path):
        file_path = os.path.abspath(bpy.path.abspath(file_path))
        return f'<a href="file:///{file_path}">{file_path}</a>'

class FBX_OT_OpenFilePath(bpy.types.Operator):
    bl_idname = "fbx.open_file_path"
    bl_label = "Open File Path"

    filepath: bpy.props.StringProperty()

    def execute(self, context):
        try:
            bpy.ops.wm.path_open(filepath=self.filepath)
        except Exception as e:
            self.report({'ERROR'}, f"Failed to open file path: {e}")
        return {'FINISHED'}

class FBX_OT_MessageDialog(bpy.types.Operator):
    bl_idname = "fbx.message_dialog"
    bl_label = "FBX Message Dialog"

    message: bpy.props.StringProperty()
    title: bpy.props.StringProperty()
    icon: bpy.props.StringProperty()
    filepath: bpy.props.StringProperty()

    def execute(self, context):
        return {'FINISHED'}

    def invoke(self, context, event):
        return context.window_manager.invoke_props_dialog(self)

    def draw(self, context):
        layout = self.layout
        layout.label(text=self.message)
        if self.filepath:
            op = layout.operator("fbx.open_file_path", text="Open File Path", icon='FILE_FOLDER')
            op.filepath = self.filepath

def show_message_box(self, context, message, title, icon='INFO', filepath=None):
    bpy.ops.fbx.message_dialog('INVOKE_DEFAULT', message=message, title=title, icon=icon, filepath=filepath)

class OBJECT_OT_FBXQuickExport(Operator):
    bl_idname = "object.fbx_quick_export"
    bl_label = "Quick Export FBX"
    bl_options = {'REGISTER', 'UNDO'}

    def execute(self, context):
        logging.debug("FBX Quick Export: Export operator executed")
        props = context.scene.fbx_export_props
        #selected_object = context.active_object
        exported_files = []

        # Check if a file path is selected
        if not props.filepath:
            self.report({'ERROR'}, "Please select an export path")
            show_message_box(self, context, "Please select an export path", "Export Error", 'ERROR')
            return {'CANCELLED'}
        
        selected_armatures = [obj for obj in bpy.context.selected_objects if obj.type == "ARMATURE"]
        if not selected_armatures:
            self.report({'ERROR'}, "Please select at least one armature")
            return {'CANCELLED'}

        for selected_object in selected_armatures:
            bpy.ops.object.select_all(action='DESELECT')
            selected_object.select_set(True)
            context.view_layer.objects.active = selected_object

            name_conflict, original_names, selected_original_names = self.clean_and_rename_objects(selected_object)

            try:
                if props.export_mesh:
                    filename = self.generate_filename(props.filename, selected_object.name, "mesh")
                    self.fbx_export(context, filename, False)
                    if os.path.exists(os.path.join(props.filepath, filename)):
                        exported_files.append(filename)

                if props.export_animation:
                    base_filename = self.generate_filename(props.filename, selected_object.name, "animation")
                    filename = self.get_next_animation_filename(props.filepath, base_filename)
                    self.fbx_export(context, filename, True)
                    if os.path.exists(os.path.join(props.filepath, filename)):
                        exported_files.append(filename)

            finally:
                if name_conflict:
                    self.restore_original_names(original_names, selected_original_names)

        # Show success message
        if exported_files:
            message = f"Successfully exported: \n" + "\n".join(exported_files)
            show_message_box(self, context, message, "Export Successful", 'INFO', filepath=props.filepath)
        else:
            show_message_box(self, context, "No files were exported.", "Export Information", 'WARNING')
        return {'FINISHED'}
    
    def generate_filename(self, user_input, selected_object_name, suffix):
        # Split the object name at the first period
        selected_object_name_base = selected_object_name.split('.')[0]
        
        # If user input is not empty, use it as a prefix
        if user_input:
            return f"{user_input}_{selected_object_name_base}_{suffix}"
        else:
            return f"{selected_object_name_base}_{suffix}"

    def get_next_animation_filename(self, filepath, base_filename):
        index = 1
        while True:
            filename = f"{base_filename}_{index:02d}.fbx"
            if not os.path.exists(os.path.join(filepath, filename)):
                return filename
            index += 1

    def clean_and_rename_objects(self, selected_object):
        obj_original_name = ""
        armature_original_name = ""
        mesh_original_name = ""
        name_conflict = False

        # Store original names of selected object and its children
        selected_original_names = {
            'obj': selected_object.name,
            'armature': selected_object.data.name,
            'mesh': selected_object.children[0].name
        }

        # Function to clean up the name by removing the number and extra period
        def clean_object_name(name):
            return re.sub(r'\.\d+\.', '.', name)

        # Clean the name of the selected object
        cleaned_armature_name = clean_object_name(selected_object.name)

        # Check if any other objects already have the cleaned name
        for obj in bpy.data.objects:
            if obj != selected_object and obj.name == cleaned_armature_name:
                name_conflict = True

                # Rename the conflicting object and its children
                obj_original_name = obj.name #save the conflicting object name
                armature_original_name = bpy.data.armatures[obj.name].name #save the amrmature conflict name

                bpy.data.armatures[obj.name].name = bpy.data.armatures[obj.name].name + "_temp" #rename the conflicting armature with _temp
                obj.name = obj.name + "_temp" #rename the conflicting object with _temp

                #navigate to any child mesh objects and rename them with _temp
                if obj.children:
                    for child in obj.children:
                        if child.type == 'MESH':
                            mesh_original_name = child.name # save the mesh name
                            child.name = child.name + "_temp"

        # Rename the selected object and its children
        bpy.data.armatures[selected_object.name].name = cleaned_armature_name
        selected_object.name = cleaned_armature_name

        for child in selected_object.children:
            if child.type == 'MESH':
                child.name = clean_object_name(child.name)

        return name_conflict, {
            'obj': obj_original_name,
            'armature': armature_original_name,
            'mesh': mesh_original_name
        }, selected_original_names

    def restore_original_names(self, original_names, selected_original_names):
        logging.debug("FBX Quick Export: Restoring original names")
        
        # First, restore the names of selected object and its children
        bpy.data.objects[original_names['obj']].name = selected_original_names['obj']
        bpy.data.armatures[original_names['armature']].name = selected_original_names['armature']
        bpy.data.objects[original_names['mesh']].name = selected_original_names['mesh']

        # Then, restore the names of conflicting objects
        if original_names['obj']:
            if original_names['obj'] + "_temp" in bpy.data.objects:
                bpy.data.objects[original_names['obj'] + "_temp"].name = original_names['obj']
        if original_names['armature']:
            if original_names['armature'] + "_temp" in bpy.data.armatures:
                bpy.data.armatures[original_names['armature'] + "_temp"].name = original_names['armature']
        if original_names['mesh']:
            if original_names['mesh'] + "_temp" in bpy.data.objects:
                bpy.data.objects[original_names['mesh'] + "_temp"].name = original_names['mesh']

    def fbx_export(self, context, filename, export_anim_bool):
        props = context.scene.fbx_export_props
        filepath = os.path.join(props.filepath, filename)

        bpy.ops.export_scene.fbx(
            filepath=filepath,
            check_existing=False,
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
        # Ensure file was created
        if os.path.exists(filepath):
            logging.debug(f"FBX Quick Export: Confirmed {filename} exists")
        else:
            logging.error(f"FBX Quick Export: Failed to create {filename}")

class VIEW3D_PT_FBXQuickExportPanel(Panel):
    bl_space_type = 'VIEW_3D'
    bl_region_type = 'UI'
    bl_category = "FBX Quick Export"
    bl_label = "FBX Quick Export"

    def draw(self, context):
        layout = self.layout
        props = context.scene.fbx_export_props

        layout.prop(props, "filepath")
        layout.prop(props, "filename")
        layout.prop(props, "export_mesh")
        layout.prop(props, "export_animation")

        if props.preview_filename:
            box = layout.box()
            #box.label(text="Preview:")
            for line in props.preview_filename.split('\n'):
                box.label(text=line)

        layout.operator("object.fbx_quick_export")

classes = (
    FBXExportProperties,
    OBJECT_OT_FBXQuickExport,
    FBX_OT_MessageDialog,
    FBX_OT_OpenFilePath,
    VIEW3D_PT_FBXQuickExportPanel,
)

def register():
    logging.debug("FBX Quick Export: Registering classes...")
    for cls in classes:
        bpy.utils.register_class(cls)
        logging.debug(f"FBX Quick Export: Registered {cls.__name__}")
    bpy.types.Scene.fbx_export_props = PointerProperty(type=FBXExportProperties)
    bpy.app.handlers.depsgraph_update_post.append(update_filename_on_object_select)
    logging.debug("FBX Quick Export: Registration complete")

def unregister():
    logging.debug("FBX Quick Export: Unregistering classes...")
    for cls in reversed(classes):
        bpy.utils.unregister_class(cls)
        logging.debug(f"FBX Quick Export: Unregistered {cls.__name__}")
    del bpy.types.Scene.fbx_export_props
    bpy.app.handlers.depsgraph_update_post.remove(update_filename_on_object_select)
    logging.debug("FBX Quick Export: Unregistration complete")

if __name__ == "__main__":
    register()