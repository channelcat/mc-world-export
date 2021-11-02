import os
from typing import IO
import tempfile

from numpy import mod
import bpy
from zipfile import ZipFile

from bpy.types import Collection, Context, Mesh, Object
from . import import_obj
from .world import VCAPWorld

from .. import amulet_nbt
from ..amulet_nbt import TAG_Compound, TAG_List, TAG_Byte_Array

class VCAPContext:
    archive: ZipFile
    collection: Collection
    context: Context

    models: dict[str, Mesh] = {}
    
    def __init__(self, archive: ZipFile, collection: Collection, context: Context) -> None:
        """Create a VCAP context

        Args:
            archive (ZipFile): Loaded VCAP archive.
            collection (Collection): Collection to import into.
            context (Context): Blender context.
        """
        self.archive = archive
        self.context = context

        self.collection = bpy.data.collections.new('vcap_import')
        collection.children.link(self.collection)
    
    def get_mesh(self, model_id: str):
        if (model_id in self.models):
            return self.models[model_id]
        else:
            return self._import_mesh(model_id)

    # This is extremely hacky due to how hard-coded the obj importer is. Should recode that at some point.
    def _import_mesh(self, model_id: str):        
        tmpname = self.archive.extract(member=f'mesh/{model_id}.obj', path=tempfile.gettempdir())
        print("Extracted to "+tmpname)
        objects: list[Object] = import_obj.load(context=self.context, filepath=tmpname)
        if (len(objects) > 1):
            raise RuntimeError("Only one obj object is allowed per model in VCAP.")
        
        obj = objects[0]
        mesh: Mesh = obj.data
        if not isinstance(mesh, Mesh):
            raise RuntimeError("Imported object is not a mesh.")

        self.models[model_id] = mesh
        bpy.data.objects.remove(obj, do_unlink=True)
        return mesh


def load(file: str, collection: Collection, context: Context):
    """Import a vcap file.

    Args:
        filename (str): File to import from.
        collection (Collection): Collection to add to.
        context (bpy.context): Blender context.
    """
    archive = ZipFile(file, 'r')
    world_dat = archive.open('world.dat')
    
    vcap_context = VCAPContext(archive, collection, context)
    loadMeshes(archive, vcap_context)
    print(vcap_context.models)
    readWorld(world_dat)
    world_dat.close

def loadMeshes(archive: ZipFile, context: VCAPContext):
    for file in archive.filelist:
        if file.filename.startswith('mesh/'):
            model_id = os.path.splitext(os.path.basename(file.filename))[0]
            context.get_mesh(model_id)

def readWorld(world_dat: IO[bytes]):
    nbt: amulet_nbt.NBTFile = amulet_nbt.load(world_dat.read(), compressed=False)
    world = VCAPWorld(nbt.value)



    frame = world.get_frame(0)

def readSection(section: TAG_Compound):
    palette: TAG_List = section['palette']
    offset: tuple[int, int, int] = (section['x'].value, section['y'].value, section['z'].value)
    blocks: TAG_Byte_Array = section['blocks']
    bblocks = blocks.value

    b = bblocks.item(1)
