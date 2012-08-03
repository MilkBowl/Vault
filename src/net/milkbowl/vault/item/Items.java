/* This file is part of Vault.

    Vault is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Vault is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Vault.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.milkbowl.vault.item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Items {

    private static final List<ItemInfo> items = new CopyOnWriteArrayList<ItemInfo>();

    static {
        items.add(new ItemInfo("Stone", new String[][]{{"ston"}, {"smoo", "sto"}}, Material.STONE));
        items.add(new ItemInfo("Grass", new String[][]{{"gras"}}, Material.GRASS));
        items.add(new ItemInfo("Dirt", new String[][]{{"dirt"}}, Material.DIRT));
        items.add(new ItemInfo("Cobblestone", new String[][]{{"cobb", "sto"}, {"cobb"}}, Material.COBBLESTONE));
        items.add(new ItemInfo("Wooden Plank", new String[][]{{"wood"}, {"wood", "plank"}}, Material.WOOD));
        items.add(new ItemInfo("Pine Plank", new String[][]{{"pine", "plank"}, {"pine", "wood"}}, Material.WOOD, (short) 1));
        items.add(new ItemInfo("Birch Plank", new String[][]{{"birch", "plank"}, {"birch", "wood"}}, Material.WOOD, (short) 2));
        items.add(new ItemInfo("Jungle Plank", new String[][]{{"jung", "plank"}, {"jung", "wood"}}, Material.WOOD, (short) 3));
        items.add(new ItemInfo("Sapling", new String[][]{{"sapling"}}, Material.SAPLING));
        items.add(new ItemInfo("Redwood Sapling", new String[][]{{"sapling", "red"}}, Material.SAPLING, (short) 1));
        items.add(new ItemInfo("Birch Sapling", new String[][]{{"sapling", "birch"}}, Material.SAPLING, (short) 2));
        items.add(new ItemInfo("Jungle Sapling", new String[][]{{"sapling", "jungle"}}, Material.SAPLING, (short) 3));
        items.add(new ItemInfo("Bedrock", new String[][]{{"rock"}}, Material.BEDROCK));
        items.add(new ItemInfo("Water", new String[][]{{"water"}}, Material.WATER));
        items.add(new ItemInfo("Lava", new String[][]{{"lava"}}, Material.LAVA));
        items.add(new ItemInfo("Sand", new String[][]{{"sand"}}, Material.SAND));
        items.add(new ItemInfo("Gravel", new String[][]{{"gravel"}}, Material.GRAVEL));
        items.add(new ItemInfo("Gold Ore", new String[][]{{"ore", "gold"}}, Material.GOLD_ORE));
        items.add(new ItemInfo("Iron Ore", new String[][]{{"ore", "iron"}}, Material.IRON_ORE));
        items.add(new ItemInfo("Coal Ore", new String[][]{{"ore", "coal"}}, Material.COAL_ORE));
        items.add(new ItemInfo("Log", new String[][]{{"log"}}, Material.LOG));
        items.add(new ItemInfo("Redwood Log", new String[][]{{"red", "log"}, {"red", "wood"}}, Material.LOG, (short) 1));
        items.add(new ItemInfo("Birch Log", new String[][]{{"birch"}, {"birch", "log"}}, Material.LOG, (short) 2));
        items.add(new ItemInfo("Jungle Log", new String[][]{{"jung", "log"}}, Material.LOG, (short) 3));
        items.add(new ItemInfo("Leaves Block", new String[][]{{"blo", "leaf"}, {"blo", "leaves"}}, Material.LEAVES));
        items.add(new ItemInfo("Redwood Leaves Block", new String[][]{{"blo", "lea", "red"}}, Material.LEAVES, (short) 1));
        items.add(new ItemInfo("Birch Leaves Block", new String[][]{{"blo", "lea", "birch"}}, Material.LEAVES, (short) 2));
        items.add(new ItemInfo("Jungle Leaves Block", new String[][]{{"blo", "lea", "jung"}}, Material.LEAVES, (short) 3));
        items.add(new ItemInfo("Leaves", new String[][]{{"leaf"}, {"leaves"}}, Material.LEAVES, (short) 4));
        items.add(new ItemInfo("Redwood Leaves", new String[][]{{"lea", "red"}}, Material.LEAVES, (short) 5));
        items.add(new ItemInfo("Birch Leaves", new String[][]{{"lea", "birch"}}, Material.LEAVES, (short) 6));
        items.add(new ItemInfo("Jungle Leaves", new String[][]{{"lea", "jung"}}, Material.LEAVES, (short) 7));
        items.add(new ItemInfo("Sponge", new String[][]{{"sponge"}}, Material.SPONGE));
        items.add(new ItemInfo("Glass", new String[][]{{"glas"}, {"sili"}}, Material.GLASS));
        items.add(new ItemInfo("Lapis Lazuli Ore", new String[][]{{"laz", "ore"}, {"ore", "lapi"}}, Material.LAPIS_ORE));
        items.add(new ItemInfo("Lapis Lazuli Block", new String[][]{{"lazu", "bl"}, {"blo", "lapi"}}, Material.LAPIS_BLOCK));
        items.add(new ItemInfo("Dispenser", new String[][]{{"dispen"}}, Material.DISPENSER));
        items.add(new ItemInfo("Sandstone", new String[][]{{"sand", "st"}}, Material.SANDSTONE));
        items.add(new ItemInfo("Decorative Sandstone", new String[][]{{"dec", "sand", "sto"}}, Material.SANDSTONE, (short) 1));
        items.add(new ItemInfo("Smooth Sandstone", new String[][]{{"smoo", "sand", "sto"}}, Material.SANDSTONE, (short) 2));
        items.add(new ItemInfo("Note Block", new String[][]{{"note"}}, Material.NOTE_BLOCK));
        items.add(new ItemInfo("Bed Block", new String[][]{{"block", "bed"}}, Material.BED_BLOCK));
        items.add(new ItemInfo("Powered Rail", new String[][]{{"rail", "pow"}, {"trac", "pow"}, {"boost"}}, Material.POWERED_RAIL));
        items.add(new ItemInfo("Detector Rail", new String[][]{{"rail", "det"}, {"trac", "det"}, {"detec"}}, Material.DETECTOR_RAIL));
        items.add(new ItemInfo("Sticky Piston", new String[][]{{"stic", "pis"}}, Material.PISTON_STICKY_BASE));
        items.add(new ItemInfo("Web", new String[][]{{"web"}, {"cobw"}}, Material.WEB));
        items.add(new ItemInfo("Dead Shrub", new String[][]{{"dead", "shru"}, {"dese", "shru"}}, Material.LONG_GRASS, (short) 0));
        items.add(new ItemInfo("Tall Grass", new String[][]{{"tall", "gras"}, {"long", "gras"}}, Material.LONG_GRASS, (short) 1));
        items.add(new ItemInfo("Fern", new String[][]{{"fern"}}, Material.LONG_GRASS, (short) 2));
        items.add(new ItemInfo("Piston", new String[][]{{"pist"}}, Material.PISTON_BASE));
        items.add(new ItemInfo("White Wool", new String[][]{{"wool", "whit"}, {"wool"}}, Material.WOOL));
        items.add(new ItemInfo("Orange Wool", new String[][]{{"wool", "ora"}}, Material.WOOL, (short) 1));
        items.add(new ItemInfo("Magenta Wool", new String[][]{{"wool", "mag"}}, Material.WOOL, (short) 2));
        items.add(new ItemInfo("Light Blue Wool", new String[][]{{"wool", "lig", "blue"}}, Material.WOOL, (short) 3));
        items.add(new ItemInfo("Yellow Wool", new String[][]{{"wool", "yell"}}, Material.WOOL, (short) 4));
        items.add(new ItemInfo("Light Green Wool", new String[][]{{"wool", "lig", "gree"}, {"wool", "gree"}}, Material.WOOL, (short) 5));
        items.add(new ItemInfo("Pink Wool", new String[][]{{"wool", "pink"}}, Material.WOOL, (short) 6));
        items.add(new ItemInfo("Gray Wool", new String[][]{{"wool", "gray"}, {"wool", "grey"}}, Material.WOOL, (short) 7));
        items.add(new ItemInfo("Light Gray Wool", new String[][]{{"lig", "wool", "gra"}, {"lig", "wool", "gre"}}, Material.WOOL, (short) 8));
        items.add(new ItemInfo("Cyan Wool", new String[][]{{"wool", "cya"}}, Material.WOOL, (short) 9));
        items.add(new ItemInfo("Purple Wool", new String[][]{{"wool", "pur"}}, Material.WOOL, (short) 10));
        items.add(new ItemInfo("Blue Wool", new String[][]{{"wool", "blue"}}, Material.WOOL, (short) 11));
        items.add(new ItemInfo("Brown Wool", new String[][]{{"wool", "brow"}}, Material.WOOL, (short) 12));
        items.add(new ItemInfo("Dark Green Wool", new String[][]{{"wool", "dar", "gree"}, {"wool", "gree"}}, Material.WOOL, (short) 13));
        items.add(new ItemInfo("Red Wool", new String[][]{{"wool", "red"}}, Material.WOOL, (short) 14));
        items.add(new ItemInfo("Black Wool", new String[][]{{"wool", "bla"}}, Material.WOOL, (short) 15));
        items.add(new ItemInfo("Dandelion", new String[][]{{"flow", "yell"}, {"dande"}}, Material.YELLOW_FLOWER));
        items.add(new ItemInfo("Red Rose", new String[][]{{"flow", "red"}, {"rose"}}, Material.RED_ROSE));
        items.add(new ItemInfo("Brown Mushroom", new String[][]{{"mush", "bro"}}, Material.BROWN_MUSHROOM));
        items.add(new ItemInfo("Red Mushroom", new String[][]{{"mush", "red"}}, Material.RED_MUSHROOM));
        items.add(new ItemInfo("Gold Block", new String[][]{{"gold", "bl"}}, Material.GOLD_BLOCK));
        items.add(new ItemInfo("Iron Block", new String[][]{{"iron", "bl"}}, Material.IRON_BLOCK));
        items.add(new ItemInfo("Double Stone Slab", new String[][]{{"doub", "slab"}, {"doub", "slab", "sto"}, {"doub", "step", "sto"}}, Material.DOUBLE_STEP));
        items.add(new ItemInfo("Double Sandstone Slab", new String[][]{{"doub", "slab", "sand", "sto"}, {"doub", "step", "sand", "sto"}}, Material.DOUBLE_STEP, (short) 1));
        items.add(new ItemInfo("Double Wooden Stone Slab", new String[][]{{"doub", "slab", "woo"}, {"doub", "step", "woo"}}, Material.DOUBLE_STEP, (short) 2));
        items.add(new ItemInfo("Double Cobblestone Slab", new String[][]{{"doub", "slab", "cob", "sto"}, {"doub", "slab", "cob"}, {"doub", "step", "cob"}}, Material.DOUBLE_STEP, (short) 3));
        items.add(new ItemInfo("Double Brick Slab", new String[][]{{"doub", "slab", "bri"}}, Material.DOUBLE_STEP, (short) 4));
        items.add(new ItemInfo("Double Stone Brick Slab", new String[][]{{"doub", "slab", "smoo"}, {"doub", "slab", "sto", "bri"}}, Material.DOUBLE_STEP, (short) 5));
        items.add(new ItemInfo("Stone Slab", new String[][]{{"slab", "sto"}, {"slab"}, {"step", "ston"}}, Material.STEP));
        items.add(new ItemInfo("Sandstone Slab", new String[][]{{"slab", "sand", "sto"}, {"step", "sand", "sto"}}, Material.STEP, (short) 1));
        items.add(new ItemInfo("Wooden Stone Slab", new String[][]{{"slab", "woo"}, {"step", "woo"}}, Material.STEP, (short) 2));
        items.add(new ItemInfo("Cobblestone Slab", new String[][]{{"slab", "cob", "sto"}, {"slab", "cob"}}, Material.STEP, (short) 3));
        items.add(new ItemInfo("Brick Slab", new String[][]{{"slab", "bri"}}, Material.STEP, (short) 4));
        items.add(new ItemInfo("Stone Brick Slab", new String[][]{{"slab", "sto", "bri"}}, Material.STEP, (short) 5));
        items.add(new ItemInfo("Brick", new String[][]{{"bric"}}, Material.BRICK));
        items.add(new ItemInfo("TNT", new String[][]{{"tnt"}, {"boom"}}, Material.TNT));
        items.add(new ItemInfo("Bookshelf", new String[][]{{"bookshe"}, {"book", "she"}}, Material.BOOKSHELF));
        items.add(new ItemInfo("Moss Stone", new String[][]{{"moss", "sto"}, {"moss"}}, Material.MOSSY_COBBLESTONE));
        items.add(new ItemInfo("Obsidian", new String[][]{{"obsi"}}, Material.OBSIDIAN));
        items.add(new ItemInfo("Torch", new String[][]{{"torc"}}, Material.TORCH));
        items.add(new ItemInfo("Fire", new String[][]{{"fire"}}, Material.FIRE));
        items.add(new ItemInfo("Monster Spawner", new String[][]{{"spawn"}}, Material.MOB_SPAWNER));
        items.add(new ItemInfo("Wooden Stairs", new String[][]{{"stair", "wood"}}, Material.WOOD_STAIRS));
        items.add(new ItemInfo("Chest", new String[][]{{"chest"}}, Material.CHEST));
        items.add(new ItemInfo("Diamond Ore", new String[][]{{"ore", "diam"}}, Material.DIAMOND_ORE));
        items.add(new ItemInfo("Diamond Block", new String[][]{{"diam", "bl"}}, Material.DIAMOND_BLOCK));
        items.add(new ItemInfo("Crafting Table", new String[][]{{"benc"}, {"squa"}, {"craft"}}, Material.WORKBENCH));
        items.add(new ItemInfo("Farmland", new String[][]{{"soil"}, {"farm"}}, Material.SOIL));
        items.add(new ItemInfo("Furnace", new String[][]{{"furna"}, {"cooke"}}, Material.FURNACE));
        items.add(new ItemInfo("Ladder", new String[][]{{"ladd"}}, Material.LADDER));
        items.add(new ItemInfo("Rails", new String[][]{{"rail"}, {"trac"}}, Material.RAILS));
        items.add(new ItemInfo("Cobblestone Stairs", new String[][]{{"stair", "cob", "sto"}, {"stair", "cob"}}, Material.COBBLESTONE_STAIRS));
        items.add(new ItemInfo("Lever", new String[][]{{"lever"}, {"switc"}}, Material.LEVER));
        items.add(new ItemInfo("Stone Pressure Plate", new String[][]{{"pres", "plat", "ston"}}, Material.STONE_PLATE));
        items.add(new ItemInfo("Wooden Pressure Plate", new String[][]{{"pres", "plat", "wood"}}, Material.WOOD_PLATE));
        items.add(new ItemInfo("Redstone Ore", new String[][]{{"ore", "red"}, {"ore", "rs"}}, Material.REDSTONE_ORE));
        items.add(new ItemInfo("Redstone Torch", new String[][]{{"torc", "red"}, {"torc", "rs"}}, Material.REDSTONE_TORCH_ON));
        items.add(new ItemInfo("Stone Button", new String[][]{{"stone", "button"}, {"button"}}, Material.STONE_BUTTON));
        items.add(new ItemInfo("Snow Tile", new String[][]{{"tile", "snow"}, {"snow", "slab"}}, Material.SNOW));
        items.add(new ItemInfo("Ice", new String[][]{{"ice"}}, Material.ICE));
        items.add(new ItemInfo("Snow Block", new String[][]{{"snow"}}, Material.SNOW_BLOCK));
        items.add(new ItemInfo("Cactus", new String[][]{{"cact"}}, Material.CACTUS));
        items.add(new ItemInfo("Clay Block", new String[][]{{"clay", "blo"}}, Material.CLAY));
        items.add(new ItemInfo("Jukebox", new String[][]{{"jukeb"}}, Material.JUKEBOX));
        items.add(new ItemInfo("Fence", new String[][]{{"fence"}}, Material.FENCE));
        items.add(new ItemInfo("Pumpkin", new String[][]{{"pump"}}, Material.PUMPKIN));
        items.add(new ItemInfo("Netherrack", new String[][]{{"netherr"}, {"netherst"}, {"hellst"}}, Material.NETHERRACK));
        items.add(new ItemInfo("Soul Sand", new String[][]{{"soul", "sand"}, {"soul"}, {"slowsa"}, {"nether", "mud"}, {"slow", "sand"}, {"quick", "sand"}, {"mud"}}, Material.SOUL_SAND));
        items.add(new ItemInfo("Glowstone", new String[][]{{"glow", "stone"}, {"light", "stone"}}, Material.GLOWSTONE));
        items.add(new ItemInfo("Portal", new String[][]{{"port"}}, Material.PORTAL));
        items.add(new ItemInfo("Jack-O-Lantern", new String[][]{{"jack"}, {"lante"}}, Material.JACK_O_LANTERN));
        items.add(new ItemInfo("Trapdoor", new String[][]{{"trap", "doo"}, {"hatc"}}, Material.TRAP_DOOR));
        items.add(new ItemInfo("Monster Egg", new String[][]{{"mon", "egg"}, {"hid", "silver"}}, Material.MONSTER_EGGS));
        items.add(new ItemInfo("Huge Brown Mushroom", new String[][]{{"bro", "huge", "mush"}}, Material.HUGE_MUSHROOM_1));
        items.add(new ItemInfo("Huge Red Mushroom", new String[][]{{"red", "huge", "mush"}}, Material.HUGE_MUSHROOM_2));
        items.add(new ItemInfo("Stone Brick", new String[][]{{"sto bri", "smoo bri"}}, Material.SMOOTH_BRICK, (short) 0));
        items.add(new ItemInfo("Iron Fence", new String[][]{{"bars", "iron"}, {"fence", "iron"}}, Material.IRON_FENCE));
        items.add(new ItemInfo("Glass Pane", new String[][]{{"thin", "gla"}, {"pane"}, {"gla", "pane"}}, Material.THIN_GLASS));
        items.add(new ItemInfo("Melon Block", new String[][]{{"melon"}}, Material.MELON_BLOCK));
        items.add(new ItemInfo("Mossy Stone Brick", new String[][]{{"moss", "sto", "bri"}, {"moss", "smoo", "bri"}, {"moss", "smoo"}, {"moss", "sto"}}, Material.SMOOTH_BRICK, (short) 1));
        items.add(new ItemInfo("Cracked Stone Brick", new String[][]{{"cra", "sto", "bri"}, {"cra", "sto"}, {"cra", "smoo", "bri"}, {"cra", "smoo"}}, Material.SMOOTH_BRICK, (short) 2));
        items.add(new ItemInfo("Circle Stone Brick", new String[][]{{"circ", "sto", "bri"}, {"cir", "sto"}, {"cir", "smoo", "bri"}}, Material.SMOOTH_BRICK, (short) 3));
        items.add(new ItemInfo("Brick Stairs", new String[][]{{"stair", "bri"}}, Material.BRICK_STAIRS));
        items.add(new ItemInfo("Fence Gate", new String[][]{{"gate", "fen"}, {"gate"}}, Material.FENCE_GATE));
        items.add(new ItemInfo("Vines", new String[][]{{"vine"}, {"ivy"}}, Material.VINE));
        items.add(new ItemInfo("Stone Brick Stairs", new String[][]{{"stair", "sto", "bri"}, {"stair", "sto"}, {"stair", "smoo", "bri"}, {"stair", "smoo"}}, Material.SMOOTH_STAIRS));
        items.add(new ItemInfo("Iron Shovel", new String[][]{{"shov", "ir"}, {"spad", "ir"}}, Material.IRON_SPADE));
        items.add(new ItemInfo("Iron Pickaxe", new String[][]{{"pick", "ir"}}, Material.IRON_PICKAXE));
        items.add(new ItemInfo("Iron Axe", new String[][]{{"axe", "ir"}}, Material.IRON_AXE));
        items.add(new ItemInfo("Flint and Steel", new String[][]{{"steel"}, {"lighter"}, {"flin", "ste"}}, Material.FLINT_AND_STEEL));
        items.add(new ItemInfo("Apple", new String[][]{{"appl"}}, Material.APPLE));
        items.add(new ItemInfo("Bow", new String[][]{{"bow"}}, Material.BOW));
        items.add(new ItemInfo("Arrow", new String[][]{{"arro"}}, Material.ARROW));
        items.add(new ItemInfo("Coal", new String[][]{{"coal"}}, Material.COAL));
        items.add(new ItemInfo("Charcoal", new String[][]{{"char", "coal"}, {"char"}}, Material.COAL, (short) 1));
        items.add(new ItemInfo("Diamond", new String[][]{{"diamo"}}, Material.DIAMOND));
        items.add(new ItemInfo("Iron Ingot", new String[][]{{"ingo", "ir"}, {"iron"}}, Material.IRON_INGOT));
        items.add(new ItemInfo("Gold Ingot", new String[][]{{"ingo", "go"}, {"gold"}}, Material.GOLD_INGOT));
        items.add(new ItemInfo("Iron Sword", new String[][]{{"swor", "ir"}}, Material.IRON_SWORD));
        items.add(new ItemInfo("Wooden Sword", new String[][]{{"swor", "woo"}}, Material.WOOD_SWORD));
        items.add(new ItemInfo("Wooden Shovel", new String[][]{{"shov", "wo"}, {"spad", "wo"}}, Material.WOOD_SPADE));
        items.add(new ItemInfo("Wooden Pickaxe", new String[][]{{"pick", "woo"}}, Material.WOOD_PICKAXE));
        items.add(new ItemInfo("Wooden Axe", new String[][]{{"axe", "woo"}}, Material.WOOD_AXE));
        items.add(new ItemInfo("Stone Sword", new String[][]{{"swor", "sto"}}, Material.STONE_SWORD));
        items.add(new ItemInfo("Stone Shovel", new String[][]{{"shov", "sto"}, {"spad", "sto"}}, Material.STONE_SPADE));
        items.add(new ItemInfo("Stone Pickaxe", new String[][]{{"pick", "sto"}}, Material.STONE_PICKAXE));
        items.add(new ItemInfo("Stone Axe", new String[][]{{"axe", "sto"}}, Material.STONE_AXE));
        items.add(new ItemInfo("Diamond Sword", new String[][]{{"swor", "dia"}}, Material.DIAMOND_SWORD));
        items.add(new ItemInfo("Diamond Shovel", new String[][]{{"shov", "dia"}, {"spad", "dia"}}, Material.DIAMOND_SPADE));
        items.add(new ItemInfo("Diamond Pickaxe", new String[][]{{"pick", "dia"}}, Material.DIAMOND_PICKAXE));
        items.add(new ItemInfo("Diamond Axe", new String[][]{{"axe", "dia"}}, Material.DIAMOND_AXE));
        items.add(new ItemInfo("Stick", new String[][]{{"stic"}}, Material.STICK));
        items.add(new ItemInfo("Bowl", new String[][]{{"bo", "wl"}}, Material.BOWL));
        items.add(new ItemInfo("Mushroom Soup", new String[][]{{"soup"}}, Material.MUSHROOM_SOUP));
        items.add(new ItemInfo("Gold Sword", new String[][]{{"swor", "gol"}}, Material.GOLD_SWORD));
        items.add(new ItemInfo("Gold Shovel", new String[][]{{"shov", "gol"}, {"spad", "gol"}}, Material.GOLD_SPADE));
        items.add(new ItemInfo("Gold Pickaxe", new String[][]{{"pick", "gol"}}, Material.GOLD_PICKAXE));
        items.add(new ItemInfo("Gold Axe", new String[][]{{"axe", "gol"}}, Material.GOLD_AXE));
        items.add(new ItemInfo("String", new String[][]{{"stri"}}, Material.STRING));
        items.add(new ItemInfo("Feather", new String[][]{{"feat"}}, Material.FEATHER));
        items.add(new ItemInfo("Gunpowder", new String[][]{{"gun"}, {"sulph"}}, Material.SULPHUR));
        items.add(new ItemInfo("Wooden Hoe", new String[][]{{"hoe", "wo"}}, Material.WOOD_HOE));
        items.add(new ItemInfo("Stone Hoe", new String[][]{{"hoe", "sto"}}, Material.STONE_HOE));
        items.add(new ItemInfo("Iron Hoe", new String[][]{{"hoe", "iro"}}, Material.IRON_HOE));
        items.add(new ItemInfo("Diamond Hoe", new String[][]{{"hoe", "dia"}}, Material.DIAMOND_HOE));
        items.add(new ItemInfo("Gold Hoe", new String[][]{{"hoe", "go"}}, Material.GOLD_HOE));
        items.add(new ItemInfo("Seeds", new String[][]{{"seed"}}, Material.SEEDS));
        items.add(new ItemInfo("Wheat", new String[][]{{"whea"}}, Material.WHEAT));
        items.add(new ItemInfo("Bread", new String[][]{{"brea"}}, Material.BREAD));
        items.add(new ItemInfo("Leather Cap", new String[][]{{"cap", "lea"}, {"hat", "lea"}, {"helm", "lea"}}, Material.LEATHER_HELMET));
        items.add(new ItemInfo("Leather Tunic", new String[][]{{"tun", "lea"}, {"ches", "lea"}}, Material.LEATHER_CHESTPLATE));
        items.add(new ItemInfo("Leather Pants", new String[][]{{"pan", "lea"}, {"trou", "lea"}, {"leg", "lea"}}, Material.LEATHER_LEGGINGS));
        items.add(new ItemInfo("Leather Boots", new String[][]{{"boo", "lea"}}, Material.LEATHER_BOOTS));
        items.add(new ItemInfo("Chainmail Helmet", new String[][]{{"cap", "cha"}, {"hat", "cha"}, {"helm", "cha"}}, Material.CHAINMAIL_HELMET));
        items.add(new ItemInfo("Chainmail Chestplate", new String[][]{{"tun", "cha"}, {"ches", "cha"}}, Material.CHAINMAIL_CHESTPLATE));
        items.add(new ItemInfo("Chainmail Leggings", new String[][]{{"pan", "cha"}, {"trou", "cha"}, {"leg", "cha"}}, Material.CHAINMAIL_LEGGINGS));
        items.add(new ItemInfo("Chainmail Boots", new String[][]{{"boo", "cha"}}, Material.CHAINMAIL_BOOTS));
        items.add(new ItemInfo("Iron Helmet", new String[][]{{"cap", "ir"}, {"hat", "ir"}, {"helm", "ir"}}, Material.IRON_HELMET));
        items.add(new ItemInfo("Iron Chestplate", new String[][]{{"tun", "ir"}, {"ches", "ir"}}, Material.IRON_CHESTPLATE));
        items.add(new ItemInfo("Iron Leggings", new String[][]{{"pan", "ir"}, {"trou", "ir"}, {"leg", "ir"}}, Material.IRON_LEGGINGS));
        items.add(new ItemInfo("Iron Boots", new String[][]{{"boo", "ir"}}, Material.IRON_BOOTS));
        items.add(new ItemInfo("Diamond Helmet", new String[][]{{"cap", "dia"}, {"hat", "dia"}, {"helm", "dia"}}, Material.DIAMOND_HELMET));
        items.add(new ItemInfo("Diamond Chestplate", new String[][]{{"tun", "dia"}, {"ches", "dia"}}, Material.DIAMOND_CHESTPLATE));
        items.add(new ItemInfo("Diamond Leggings", new String[][]{{"pan", "dia"}, {"trou", "dia"}, {"leg", "dia"}}, Material.DIAMOND_LEGGINGS));
        items.add(new ItemInfo("Diamond Boots", new String[][]{{"boo", "dia"}}, Material.DIAMOND_BOOTS));
        items.add(new ItemInfo("Gold Helmet", new String[][]{{"cap", "go"}, {"hat", "go"}, {"helm", "go"}}, Material.GOLD_HELMET));
        items.add(new ItemInfo("Gold Chestplate", new String[][]{{"tun", "go"}, {"ches", "go"}}, Material.GOLD_CHESTPLATE));
        items.add(new ItemInfo("Gold Leggings", new String[][]{{"pan", "go"}, {"trou", "go"}, {"leg", "go"}}, Material.GOLD_LEGGINGS));
        items.add(new ItemInfo("Gold Boots", new String[][]{{"boo", "go"}}, Material.GOLD_BOOTS));
        items.add(new ItemInfo("Flint", new String[][]{{"flin"}}, Material.FLINT));
        items.add(new ItemInfo("Raw Porkchop", new String[][]{{"pork"}, {"ham"}}, Material.PORK));
        items.add(new ItemInfo("Cooked Porkchop", new String[][]{{"pork", "cook"}, {"baco"}}, Material.GRILLED_PORK));
        items.add(new ItemInfo("Paintings", new String[][]{{"paint"}}, Material.PAINTING));
        items.add(new ItemInfo("Golden Apple", new String[][]{{"appl", "go"}}, Material.GOLDEN_APPLE));
        items.add(new ItemInfo("Sign", new String[][]{{"sign"}}, Material.SIGN));
        items.add(new ItemInfo("Wooden Door", new String[][]{{"door", "wood"}, {"door"}}, Material.WOOD_DOOR));
        items.add(new ItemInfo("Bucket", new String[][]{{"buck"}, {"bukk"}}, Material.BUCKET));
        items.add(new ItemInfo("Water Bucket", new String[][]{{"water", "buck"}}, Material.WATER_BUCKET));
        items.add(new ItemInfo("Lava Bucket", new String[][]{{"lava", "buck"}}, Material.LAVA_BUCKET));
        items.add(new ItemInfo("Minecart", new String[][]{{"cart"}}, Material.MINECART));
        items.add(new ItemInfo("Saddle", new String[][]{{"sad"}, {"pig"}}, Material.SADDLE));
        items.add(new ItemInfo("Iron Door", new String[][]{{"door", "iron"}}, Material.IRON_DOOR));
        items.add(new ItemInfo("Redstone Dust", new String[][]{{"red", "ston", "dust"}, {"red", "ston"}, {"dust", "rs"}, {"dust", "red"}, {"reds"}}, Material.REDSTONE));
        items.add(new ItemInfo("Snowball", new String[][]{{"snow", "ball"}}, Material.SNOW_BALL));
        items.add(new ItemInfo("Boat", new String[][]{{"boat"}}, Material.BOAT));
        items.add(new ItemInfo("Leather", new String[][]{{"lea"}, {"hide"}}, Material.LEATHER));
        items.add(new ItemInfo("Milk Bucket", new String[][]{{"milk"}}, Material.MILK_BUCKET));
        items.add(new ItemInfo("Clay Brick", new String[][]{{"bric", "cl"}, {"sin", "bric"}}, Material.CLAY_BRICK));
        items.add(new ItemInfo("Clay", new String[][]{{"cla"}}, Material.CLAY_BALL));
        items.add(new ItemInfo("Sugar Cane", new String[][]{{"reed"}, {"cane"}}, Material.SUGAR_CANE));
        items.add(new ItemInfo("Paper", new String[][]{{"pape"}}, Material.PAPER));
        items.add(new ItemInfo("Book", new String[][]{{"book"}}, Material.BOOK));
        items.add(new ItemInfo("Slimeball", new String[][]{{"slime"}}, Material.SLIME_BALL));
        items.add(new ItemInfo("Storage Minecart", new String[][]{{"cart", "sto"}, {"cart", "che"}, {"cargo"}}, Material.STORAGE_MINECART));
        items.add(new ItemInfo("Powered Minecart", new String[][]{{"cart", "pow"}, {"engine"}}, Material.POWERED_MINECART));
        items.add(new ItemInfo("Egg", new String[][]{{"egg"}}, Material.EGG));
        items.add(new ItemInfo("Compass", new String[][]{{"comp"}}, Material.COMPASS));
        items.add(new ItemInfo("Fishing Rod", new String[][]{{"rod"}, {"pole"}}, Material.FISHING_ROD));
        items.add(new ItemInfo("Clock", new String[][]{{"cloc"}, {"watc"}}, Material.WATCH));
        items.add(new ItemInfo("Glowstone Dust", new String[][]{{"glow", "sto", "dus"}, {"glow", "dus"}, {"ligh", "dust"}}, Material.GLOWSTONE_DUST));
        items.add(new ItemInfo("Raw Fish", new String[][]{{"fish"}}, Material.RAW_FISH));
        items.add(new ItemInfo("Cooked Fish", new String[][]{{"fish", "coo"}, {"kipper"}}, Material.COOKED_FISH));
        items.add(new ItemInfo("Ink Sac", new String[][]{{"ink"}, {"dye", "bla"}}, Material.INK_SACK));
        items.add(new ItemInfo("Red Dye", new String[][]{{"dye", "red"}, {"pain", "red"}, {"pet", "ros"}, {"pet", "red"}}, Material.INK_SACK, (short) 1));
        items.add(new ItemInfo("Cactus Green", new String[][]{{"cact", "gree"}, {"dye", "gree"}, {"pain", "gree"}}, Material.INK_SACK, (short) 2));
        items.add(new ItemInfo("Cocoa Beans", new String[][]{{"bean"}, {"choco"}, {"cocoa"}, {"dye", "bro"}, {"pain", "bro"}}, Material.INK_SACK, (short) 3));
        items.add(new ItemInfo("Lapis Lazuli", new String[][]{{"dye", "lapi"}, {"dye", "blu"}, {"pain", "blu"}}, Material.INK_SACK, (short) 4));
        items.add(new ItemInfo("Purple Dye", new String[][]{{"dye", "pur"}, {"pain", "pur"}}, Material.INK_SACK, (short) 5));
        items.add(new ItemInfo("Cyan Dye", new String[][]{{"dye", "cya"}, {"pain", "cya"}}, Material.INK_SACK, (short) 6));
        items.add(new ItemInfo("Light Gray Dye", new String[][]{{"dye", "lig", "gra"}, {"dye", "lig", "grey"}, {"pain", "lig", "grey"}, {"pain", "lig", "grey"}}, Material.INK_SACK, (short) 7));
        items.add(new ItemInfo("Gray Dye", new String[][]{{"dye", "gra"}, {"dye", "grey"}, {"pain", "grey"}, {"pain", "grey"}}, Material.INK_SACK, (short) 8));
        items.add(new ItemInfo("Pink Dye", new String[][]{{"dye", "pin"}, {"pain", "pin"}}, Material.INK_SACK, (short) 9));
        items.add(new ItemInfo("Lime Dye", new String[][]{{"dye", "lim"}, {"pain", "lim"}, {"dye", "lig", "gree"}, {"pain", "lig", "gree"}}, Material.INK_SACK, (short) 10));
        items.add(new ItemInfo("Dandelion Yellow", new String[][]{{"dye", "yel"}, {"yel", "dan"}, {"pet", "dan"}, {"pet", "yel"}}, Material.INK_SACK, (short) 11));
        items.add(new ItemInfo("Light Blue Dye", new String[][]{{"dye", "lig", "blu"}, {"pain", "lig", "blu"}}, Material.INK_SACK, (short) 12));
        items.add(new ItemInfo("Magenta Dye", new String[][]{{"dye", "mag"}, {"pain", "mag"}}, Material.INK_SACK, (short) 13));
        items.add(new ItemInfo("Orange Dye", new String[][]{{"dye", "ora"}, {"pain", "ora"}}, Material.INK_SACK, (short) 14));
        items.add(new ItemInfo("Bone Meal", new String[][]{{"bonem"}, {"bone", "me"}, {"dye", "whi"}, {"pain", "whi"}}, Material.INK_SACK, (short) 15));
        items.add(new ItemInfo("Bone", new String[][]{{"bone"}, {"femur"}}, Material.BONE));
        items.add(new ItemInfo("Sugar", new String[][]{{"suga"}}, Material.SUGAR));
        items.add(new ItemInfo("Cake", new String[][]{{"cake"}}, Material.CAKE));
        items.add(new ItemInfo("Melon Slice", new String[][]{{"sli", "melo"}}, Material.MELON));
        items.add(new ItemInfo("Pumpkin Seed", new String[][]{{"seed", "pump"}}, Material.PUMPKIN_SEEDS));
        items.add(new ItemInfo("Melon Seed", new String[][]{{"seed", "melo"}}, Material.MELON_SEEDS));
        items.add(new ItemInfo("Raw Beef", new String[][]{{"beef", "raw"}}, Material.RAW_BEEF));
        items.add(new ItemInfo("Steak", new String[][]{{"steak"}, {"beef", "coo"}}, Material.COOKED_BEEF));
        items.add(new ItemInfo("Raw Chicken", new String[][]{{"chi", "raw"}}, Material.RAW_CHICKEN));
        items.add(new ItemInfo("Cooked Chicken", new String[][]{{"chi", "coo"}}, Material.COOKED_CHICKEN));
        items.add(new ItemInfo("Rotten Flesh", new String[][]{{"flesh"}, {"rott"}}, Material.ROTTEN_FLESH));
        items.add(new ItemInfo("Bed", new String[][]{{"bed"}}, Material.BED));
        items.add(new ItemInfo("Redstone Repeater", new String[][]{{"repe", "reds"}, {"diod"}, {"repeat"}}, Material.DIODE));
        items.add(new ItemInfo("Cookie", new String[][]{{"cooki"}}, Material.COOKIE));
        items.add(new ItemInfo("Map", new String[][]{{"map"}}, Material.MAP));
        items.add(new ItemInfo("Shears", new String[][]{{"shea"}}, Material.SHEARS));
        items.add(new ItemInfo("Ender Pearl", new String[][]{{"end pea", "pear"}}, Material.ENDER_PEARL));
        items.add(new ItemInfo("Mycelium", new String[][]{{ "myc" }}, Material.MYCEL));
        items.add(new ItemInfo("Lily Pad", new String[][]{{"lil", "pad"}, {"lil", "wat"}}, Material.WATER_LILY));
        items.add(new ItemInfo("Cauldron Block", new String[][]{{ "bloc", "cauld"}}, Material.CAULDRON));
        items.add(new ItemInfo("Cauldron", new String[][]{{"cauld"}}, Material.CAULDRON_ITEM));
        items.add(new ItemInfo("Enchantment Table", new String[][]{{"ench", "tab"}}, Material.ENCHANTMENT_TABLE));
        items.add(new ItemInfo("Brewing Stand Block", new String[][] {{ "bloc", "brew", "stan" }, {"alch", "bloc"}}, Material.BREWING_STAND));
        items.add(new ItemInfo("Brewing Stand", new String[][] {{"brew", "stan"}, {"alch", "stand"}, {"alch", "tab"}}, Material.BREWING_STAND_ITEM));
        items.add(new ItemInfo("Nether Brick", new String[][] {{"neth", "bric"}}, Material.NETHER_BRICK));
        items.add(new ItemInfo("Nether Brick Stairs", new String[][] {{"neth", "stair"}, {"neth", "stai", "bric"}}, Material.NETHER_BRICK_STAIRS));
        items.add(new ItemInfo("Nether Brick Fence", new String[][]{{"neth", "fence"}, {"neth", "fence", "bric"}}, Material.NETHER_FENCE));
        items.add(new ItemInfo("Netherwarts", new String[][]{{"wart"}, {"neth", "war"}}, Material.NETHER_WARTS));
        items.add(new ItemInfo("Netherstalk", new String[][]{{"neth", "stalk"}}, Material.NETHER_STALK));
        items.add(new ItemInfo("End Portal", new String[][] {{"end", "port"}}, Material.ENDER_PORTAL));
        items.add(new ItemInfo("End Portal Frame", new String[][] {{"fram", "end", "port"}}, Material.ENDER_PORTAL_FRAME));
        items.add(new ItemInfo("End Stone", new String[][] {{"end", "ston"}}, Material.ENDER_STONE));
        items.add(new ItemInfo("Dragon Egg", new String[][] {{"drag", "egg"}}, Material.DRAGON_EGG));
        items.add(new ItemInfo("Blaze Rod", new String[][] {{"rod", "blaz"}}, Material.BLAZE_ROD));
        items.add(new ItemInfo("Ghost Tear", new String[][] {{"ghas", "tear"}}, Material.GHAST_TEAR));
        items.add(new ItemInfo("Gold Nugget", new String[][] {{"nugg", "gold"}}, Material.GOLD_NUGGET));
        items.add(new ItemInfo("Glass Bottle", new String[][] {{"bottl"}, {"glas", "bott"}, {"empt", "bott"}}, Material.GLASS_BOTTLE));
        items.add(new ItemInfo("Potion", new String[][] {{"potio"}}, Material.POTION));
        items.add(new ItemInfo("Water Bottle", new String[][] {{"wat", "bot"}}, Material.POTION, (short) 0));
        items.add(new ItemInfo("Awkward Potion", new String[][] {{"pot", "awk"}}, Material.POTION, (short) 16));
        items.add(new ItemInfo("Thick Potion", new String[][] {{"pot", "thic"}}, Material.POTION, (short) 32));
        items.add(new ItemInfo("Mundane Potion (Extended)", new String[][] {{"pot", "mund", "ext"}}, Material.POTION, (short) 64));
        items.add(new ItemInfo("Mundane Potion", new String[][] {{"pot", "mund"}}, Material.POTION, (short) 8192));
        items.add(new ItemInfo("Potion of Regeneration", new String[][] {{"pot", "rege"}}, Material.POTION, (short) 8193));
        items.add(new ItemInfo("Potion of Regeneration (Extended)", new String[][] {{"pot", "rege", "ext"}}, Material.POTION, (short) 8257));
        items.add(new ItemInfo("Potion of Regeneration II", new String[][] {{"pot", "rege", "2"}, {"pot", "rege", "ii"}}, Material.POTION, (short) 8225));
        items.add(new ItemInfo("Potion of Swiftness", new String[][] {{"pot", "swif"}, {"pot", "speed"}}, Material.POTION, (short) 8194));
        items.add(new ItemInfo("Potion of Swiftness (Extended)", new String[][] {{"pot", "swif", "ext"}, {"pot", "speed", "ext"}}, Material.POTION, (short) 8258));
        items.add(new ItemInfo("Potion of Swiftness II", new String[][] {{"pot", "swif", "2"}, {"pot", "swif", "ii"}, {"pot", "speed", "2"}, {"pot", "speed", "ii"}}, Material.POTION, (short) 8226));
        items.add(new ItemInfo("Potion of Fire Resistance", new String[][] {{"pot", "fire"}}, Material.POTION, (short) 8195));
        items.add(new ItemInfo("Potion of Fire Resistance (Extended)", new String[][] {{"pot", "fire", "ext"}}, Material.POTION, (short) 8259));
        items.add(new ItemInfo("Potion of Fire Resistance (Reverted)", new String[][] {{"pot", "fire", "rev"}}, Material.POTION, (short) 8227));
        items.add(new ItemInfo("Potion of Healing", new String[][] {{"pot", "heal"}}, Material.POTION, (short) 8197));
        items.add(new ItemInfo("Potion of Healing (Reverted)", new String[][] {{"pot", "heal", "rev"}}, Material.POTION, (short) 8261));
        items.add(new ItemInfo("Potion of Healing II", new String[][] {{"pot", "heal", "2"}, {"pot", "heal", "ii"}}, Material.POTION, (short) 8229));
        items.add(new ItemInfo("Potion of Strength", new String[][] {{"pot", "str"}}, Material.POTION, (short) 8201));
        items.add(new ItemInfo("Potion of Strength (Extended)", new String[][] {{"pot", "str", "ext"}}, Material.POTION, (short) 8265));
        items.add(new ItemInfo("Potion of Strength II", new String[][] {{"pot", "str", "2"}, {"pot", "str", "ii"}}, Material.POTION, (short) 8233));
        items.add(new ItemInfo("Potion of Poison", new String[][] {{"pot", "pois"}}, Material.POTION, (short) 8196));
        items.add(new ItemInfo("Potion of Poison (Extended)", new String[][] {{"pot", "pois", "ext"}}, Material.POTION, (short) 8260));
        items.add(new ItemInfo("Potion of Poison II", new String[][] {{"pot", "pois", "2"}, {"pot", "pois", "ii"}}, Material.POTION, (short) 8228));
        items.add(new ItemInfo("Potion of Weakness", new String[][] {{"pot", "weak"}}, Material.POTION, (short) 8200));
        items.add(new ItemInfo("Potion of Weakness (Extended)", new String[][] {{"pot", "weak", "ext"}}, Material.POTION, (short) 8264));
        items.add(new ItemInfo("Potion of Weakness (Reverted)", new String[][] {{"pot", "weak", "rev"}}, Material.POTION, (short) 8232));
        items.add(new ItemInfo("Potion of Slowness", new String[][] {{"pot", "slow"}}, Material.POTION, (short) 8202));
        items.add(new ItemInfo("Potion of Slowness (Extended)", new String[][] {{"pot", "slow", "ext"}}, Material.POTION, (short) 8266));
        items.add(new ItemInfo("Potion of Slowness (Reverted)", new String[][] {{"pot", "slow", "rev"}}, Material.POTION, (short) 8234));
        items.add(new ItemInfo("Potion of Harming", new String[][] {{"pot", "harm"}}, Material.POTION, (short) 8204));
        items.add(new ItemInfo("Potion of Harming (Reverted)", new String[][] {{"pot", "harm", "rev"}}, Material.POTION, (short) 8268));
        items.add(new ItemInfo("Potion of Harming II", new String[][] {{"pot", "harm", "2"}, {"pot", "harm", "ii"}}, Material.POTION, (short) 8236));
        items.add(new ItemInfo("Splash Mundane Potion", new String[][] {{"pot", "mund", "spl"}}, Material.POTION, (short) 16384));
        items.add(new ItemInfo("Splash Potion of Regeneration", new String[][] {{"pot", "rege", "spl"}}, Material.POTION, (short) 16385));
        items.add(new ItemInfo("Splash Potion of Regeneration (Extended)", new String[][] {{"pot", "rege", "spl", "ext"}}, Material.POTION, (short) 16449));
        items.add(new ItemInfo("Splash Potion of Regeneration II", new String[][] {{"pot", "rege", "spl", "2"}, {"pot", "rege", "spl", "ii"}}, Material.POTION, (short) 16417));
        items.add(new ItemInfo("Splash Potion of Swiftness", new String[][] {{"pot", "swif", "spl"}, {"pot", "speed", "spl"}}, Material.POTION, (short) 16386));
        items.add(new ItemInfo("Splash Potion of Swiftness (Extended)", new String[][] {{"pot", "swif", "spl", "ext"}, {"pot", "speed", "spl", "ext"}}, Material.POTION, (short) 16450));
        items.add(new ItemInfo("Splash Potion of Swiftness II", new String[][] {{"pot", "swif", "spl", "2"}, {"pot", "swif", "spl", "ii"}, {"pot", "speed", "spl", "2"}, {"pot", "speed", "spl", "ii"}}, Material.POTION, (short) 16418));
        items.add(new ItemInfo("Splash Potion of Fire Resistance", new String[][] {{"pot", "fire", "spl"}}, Material.POTION, (short) 16387));
        items.add(new ItemInfo("Splash Potion of Fire Resistance (Extended)", new String[][] {{"pot", "fire", "spl", "ext"}}, Material.POTION, (short) 16451));
        items.add(new ItemInfo("Splash Potion of Fire Resistance (Reverted)", new String[][] {{"pot", "fire", "spl", "rev"}}, Material.POTION, (short) 16419));
        items.add(new ItemInfo("Splash Potion of Healing", new String[][] {{"pot", "heal", "spl"}}, Material.POTION, (short) 16389));
        items.add(new ItemInfo("Splash Potion of Healing (Reverted)", new String[][] {{"pot", "heal", "spl", "rev"}}, Material.POTION, (short) 16453));
        items.add(new ItemInfo("Splash Potion of Healing II", new String[][] {{"pot", "heal", "spl", "2"}, {"pot", "heal", "spl", "ii"}}, Material.POTION, (short) 16421));
        items.add(new ItemInfo("Splash Potion of Strength", new String[][] {{"pot", "str", "spl"}}, Material.POTION, (short) 16393));
        items.add(new ItemInfo("Splash Potion of Strength (Extended)", new String[][] {{"pot", "str", "spl", "ext"}}, Material.POTION, (short) 16457));
        items.add(new ItemInfo("Splash Potion of Strength II", new String[][] {{"pot", "str", "spl", "2"}, {"pot", "str", "spl", "ii"}}, Material.POTION, (short) 16425));
        items.add(new ItemInfo("Splash Potion of Poison", new String[][] {{"pot", "pois", "spl"}}, Material.POTION, (short) 16388));
        items.add(new ItemInfo("Splash Potion of Poison (Extended)", new String[][] {{"pot", "pois", "spl", "ext"}}, Material.POTION, (short) 16452));
        items.add(new ItemInfo("Splash Potion of Poison II", new String[][] {{"pot", "pois", "spl", "2"}, {"pot", "pois", "spl", "ii"}}, Material.POTION, (short) 16420));
        items.add(new ItemInfo("Splash Potion of Weakness", new String[][] {{"pot", "weak", "spl"}}, Material.POTION, (short) 16392));
        items.add(new ItemInfo("Splash Potion of Weakness (Extended)", new String[][] {{"pot", "weak", "spl", "ext"}}, Material.POTION, (short) 16456));
        items.add(new ItemInfo("Splash Potion of Weakness (Reverted)", new String[][] {{"pot", "weak", "spl", "rev"}}, Material.POTION, (short) 16424));
        items.add(new ItemInfo("Splash Potion of Slowness", new String[][] {{"pot", "slow", "spl"}}, Material.POTION, (short) 16394));
        items.add(new ItemInfo("Splash Potion of Slowness (Extended)", new String[][] {{"pot", "slow", "spl", "ext"}}, Material.POTION, (short) 16458));
        items.add(new ItemInfo("Splash Potion of Slowness (Reverted)", new String[][] {{"pot", "slow", "spl", "rev"}}, Material.POTION, (short) 16426));
        items.add(new ItemInfo("Splash Potion of Harming", new String[][] {{"pot", "harm", "spl"}}, Material.POTION, (short) 16396));
        items.add(new ItemInfo("Splash Potion of Harming (Reverted)", new String[][] {{"pot", "harm", "spl", "rev"}}, Material.POTION, (short) 16460));
        items.add(new ItemInfo("Splash Potion of Harming II", new String[][] {{"pot", "harm", "spl", "2"}, {"pot", "harm", "spl", "ii"}}, Material.POTION, (short) 16428));
        items.add(new ItemInfo("Spider Eye", new String[][] {{"spid", "eye"}}, Material.SPIDER_EYE));
        items.add(new ItemInfo("Fermented Spider Eye", new String[][] {{"ferm", "spid", "eye"}}, Material.FERMENTED_SPIDER_EYE));
        items.add(new ItemInfo("Blaze Powder", new String[][] {{"powd", "blaz"}}, Material.BLAZE_POWDER));
        items.add(new ItemInfo("Magma Cream", new String[][] {{"crea", "magm"}}, Material.MAGMA_CREAM));
        items.add(new ItemInfo("Eye of Ender", new String[][] {{"end", "ey"}}, Material.EYE_OF_ENDER));
        items.add(new ItemInfo("Glistering Melon", new String[][] {{"melo", "glis"}}, Material.SPECKLED_MELON));
        items.add(new ItemInfo("Spawn Egg", new String[][] {{"spaw", "egg"}}, Material.MONSTER_EGG));
        items.add(new ItemInfo("Creeper Spawn Egg", new String[][] {{"cree", "egg"}}, Material.MONSTER_EGG, (short) 50));
        items.add(new ItemInfo("Skeleton Spawn Egg", new String[][] {{"skele", "egg"}}, Material.MONSTER_EGG, (short) 51));
        items.add(new ItemInfo("Spider Spawn Egg", new String[][] {{"spid", "egg"}}, Material.MONSTER_EGG, (short) 52));
        items.add(new ItemInfo("Zombie Spawn Egg", new String[][] {{"zomb", "egg"}}, Material.MONSTER_EGG, (short) 54));
        items.add(new ItemInfo("Slime Spawn Egg", new String[][] {{"slime", "egg"}}, Material.MONSTER_EGG, (short) 55));
        items.add(new ItemInfo("Ghast Spawn Egg", new String[][] {{"ghas", "egg"}}, Material.MONSTER_EGG, (short) 56));
        items.add(new ItemInfo("Zombie Pigman Spawn Egg", new String[][] {{"zomb", "pig", "egg"}}, Material.MONSTER_EGG, (short) 57));
        items.add(new ItemInfo("Enderman Spawn Egg", new String[][] {{"end", "man", "egg"}}, Material.MONSTER_EGG, (short) 58));
        items.add(new ItemInfo("Cave Spider Spawn Egg", new String[][] {{"cav", "spid", "egg"}}, Material.MONSTER_EGG, (short) 59));
        items.add(new ItemInfo("Silverfish Spawn Egg", new String[][] {{"silv", "fish", "egg"}}, Material.MONSTER_EGG, (short) 60));
        items.add(new ItemInfo("Blaze Spawn Egg", new String[][] {{"blaze", "egg"}}, Material.MONSTER_EGG, (short) 61));
        items.add(new ItemInfo("Magma Cube Spawn Egg", new String[][] {{"mag", "cub", "egg"}, {"neth", "slim", "egg"}}, Material.MONSTER_EGG, (short)62));
        items.add(new ItemInfo("Pig Spawn Egg", new String[][] {{"pig", "egg"}}, Material.MONSTER_EGG, (short) 90));
        items.add(new ItemInfo("Sheep Spawn Egg", new String[][] {{"shee", "egg"}}, Material.MONSTER_EGG, (short) 91));
        items.add(new ItemInfo("Cow Spawn Egg", new String[][] {{"cow", "egg"}}, Material.MONSTER_EGG, (short) 92));
        items.add(new ItemInfo("Chicken Spawn Egg", new String[][] {{"chick", "egg"}}, Material.MONSTER_EGG, (short) 93));
        items.add(new ItemInfo("Squid Spawn Egg", new String[][] {{"squ", "egg"}}, Material.MONSTER_EGG, (short) 94));
        items.add(new ItemInfo("Wolf Spawn Egg", new String[][] {{"wolf", "egg"}}, Material.MONSTER_EGG, (short) 95));
        items.add(new ItemInfo("Mooshroom Spawn Egg", new String[][] {{"moo", "room", "egg"}, {"mush", "cow", "egg"}}, Material.MONSTER_EGG, (short) 96));
        items.add(new ItemInfo("Ocelot Spawn Egg", new String[][] {{"oce", "egg"}}, Material.MONSTER_EGG, (short) 98));
        items.add(new ItemInfo("Villager Spawn Egg", new String[][] {{"vill", "egg"}, {"testi", "egg"}}, Material.MONSTER_EGG, (short) 120));
        items.add(new ItemInfo("Bottle 'o Enchanting", new String[][] {{"bot", "ench"}, {"bot", "xp"}}, Material.EXP_BOTTLE));
        items.add(new ItemInfo("Fire Charge", new String[][] {{"fir", "char"}}, Material.FIREBALL));
        items.add(new ItemInfo("Gold Music Disc", new String[][]{{"dis", "gol"}, {"rec", "gol"}}, Material.GOLD_RECORD));
        items.add(new ItemInfo("Green Music Disc", new String[][]{{"dis", "gre"}, {"rec", "gre"}}, Material.GREEN_RECORD));
        items.add(new ItemInfo("blocks Disc", new String[][] {{"block", "disc"}, {"block", "reco"}, {"3", "disc"}, {"3", "reco"}}, Material.RECORD_3));
        items.add(new ItemInfo("chirp Disc", new String[][] {{"chirp", "disc"}, {"chirp", "reco"}, {"4", "disc"}, {"4", "reco"}}, Material.RECORD_4));
        items.add(new ItemInfo("far Disc", new String[][] {{"far", "disc"}, {"far", "reco"}, {"5", "disc"}, {"5", "reco"}}, Material.RECORD_5));
        items.add(new ItemInfo("mall Disc", new String[][] {{"mall", "disc"}, {"mall", "reco"}, {"6", "disc"}, {"6", "reco"}}, Material.RECORD_6));
        items.add(new ItemInfo("mellohi Disc", new String[][] {{"mello", "disc"}, {"mello", "reco"}, {"7", "disc"}, {"7", "reco"}}, Material.RECORD_7));
        items.add(new ItemInfo("stahl Disc", new String[][] {{"stahl", "disc"}, {"stahl", "reco"}, {"8", "disc"}, {"8", "reco"}}, Material.RECORD_8));
        items.add(new ItemInfo("strad Disc", new String[][] {{"strad", "disc"}, {"strad", "reco"}, {"9", "disc"}, {"9", "reco"}}, Material.RECORD_9));
        items.add(new ItemInfo("ward Disc", new String[][] {{"ward", "disc"}, {"ward", "reco"}, {"10", "disc"}, {"10", "reco"}}, Material.RECORD_10));
        items.add(new ItemInfo("11 Disc", new String[][] {{"11", "disc"}, {"11", "reco"}}, Material.RECORD_11));
        items.add(new ItemInfo("Redstone Lamp", new String[][] {{"lamp"}, {"lamp", "red", "sto"}}, Material.REDSTONE_LAMP_OFF));
        //1.3 Blocks
        items.add(new ItemInfo("Emerald Ore", new String[][]{{"emer", "ore"}}, Material.EMERALD_ORE));
        items.add(new ItemInfo("Emerald", new String[][]{{"emer"}}, Material.EMERALD));
        items.add(new ItemInfo("Emerald Block", new String[][]{{"emer", "blo"}}, Material.EMERALD_BLOCK));
        items.add(new ItemInfo("Ender Chest", new String[][]{{"end", "ches"}}, Material.ENDER_CHEST));
        items.add(new ItemInfo("Tripwire Hook", new String[][]{{"hoo", "trip"}}, Material.TRIPWIRE_HOOK));
        items.add(new ItemInfo("Tripwire", new String[][]{{"trip"}}, Material.TRIPWIRE));
        items.add(new ItemInfo("Sandstone Stair", new String[][]{{"stair", "sand", "sto"}, {"stair", "sand"}}, Material.SANDSTONE_STAIRS));
        items.add(new ItemInfo("Double Oak Slab", new String[][]{{"doub", "slab", "oak"}, {"doub", "step", "oak"}}, Material.WOOD_DOUBLE_STEP));
        items.add(new ItemInfo("Double Spruce Slab", new String[][]{{"doub", "slab", "spru"}, {"doub", "step", "spru"}}, Material.WOOD_DOUBLE_STEP, (short) 1));
        items.add(new ItemInfo("Double Birch Slab", new String[][]{{"doub", "slab", "birc"}, {"doub", "step", "birc"}}, Material.WOOD_DOUBLE_STEP, (short) 2));
        items.add(new ItemInfo("Double Jungle-Wood Slab", new String[][]{{"doub", "slab", "jung"}, {"doub", "step", "jung"}}, Material.WOOD_DOUBLE_STEP, (short) 3));
        items.add(new ItemInfo("Oak Slab", new String[][]{{"slab", "oak"}, {"step", "oak"}}, Material.WOOD_STEP));
        items.add(new ItemInfo("Spruce Slab", new String[][]{{"slab", "spru"}, {"step", "spru"}}, Material.WOOD_STEP, (short) 1));
        items.add(new ItemInfo("Birch Slab", new String[][]{{"slab", "birc"}, {"step", "birc"}}, Material.WOOD_STEP, (short) 2));
        items.add(new ItemInfo("Jungle-Wood Slab", new String[][]{{"slab", "jung"}, {"step", "jung"}}, Material.WOOD_STEP, (short) 3));
        items.add(new ItemInfo("Book and Quill", new String[][]{{"qui", "book"}}, Material.BOOK_AND_QUILL));
        items.add(new ItemInfo("Written Book", new String[][]{{"wri", "book"}}, Material.WRITTEN_BOOK));
        
    }

    public static ItemInfo itemById(int typeId) {
        return itemByType(Material.getMaterial(typeId), (short) 0);
    }

    public static ItemInfo itemById(int typeId, short subType) {
        return itemByType(Material.getMaterial(typeId), subType);
    }

    public static ItemInfo itemByStack(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        for (ItemInfo item : items) {
            if (itemStack.getType().equals(item.getType()) && item.isDurable()) {
                return item;
            } else if (itemStack.getType().equals(item.getType()) && item.getSubTypeId() == itemStack.getDurability()) {
                return item;
            }
        }

        return null;
    }

    public static ItemInfo itemByItem(ItemInfo item) {
        for (ItemInfo i : items) {
            if (item.equals(i)) {
                return i;
            }
        }
        return null;
    }

    public static ItemInfo itemByType(Material type) {
        return itemByType(type, (short) 0);
    }
    
    public static ItemInfo itemByType(Material type, short subType) {
        for (ItemInfo item : items) {
            if (item.getType() == type && item.getSubTypeId() == subType) {
                return item;
            }
        }
        return null;
    }

    public static ItemInfo itemByString(String string) {

        // int
        Pattern pattern = Pattern.compile("(?i)^(\\d+)$");
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            return itemById(id);
        }

        // int:int
        matcher.reset();
        pattern = Pattern.compile("(?i)^(\\d+):(\\d+)$");
        matcher = pattern.matcher(string);
        if (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            short type = Short.parseShort(matcher.group(2));
            return itemById(id, type);
        }

        // name
        matcher.reset();
        pattern = Pattern.compile("(?i)^(.*)$");
        matcher = pattern.matcher(string);
        if (matcher.find()) {
            String name = matcher.group(1);
            return itemByName(name);
        }

        return null;
    }

    public static ItemInfo itemByName(ArrayList<String> search) {
        String searchString = join(search, " ");
        return itemByName(searchString);
    }
    
    public static ItemInfo[] itemByNames(ArrayList<String> search, boolean multi) {
        String searchString = join(search, " ");
        return itemsByName(searchString, multi);
    }

    /**
     * Multi-Item return search for dumping all items with the search string to the player
     *
     *
     * @param searchString
     * @param multi
     * @return Array of items found
     */
    public static ItemInfo[] itemsByName(String searchString, boolean multi) {
        if (multi == false) {
            return new ItemInfo[]{itemByName(searchString)};
        }

        ItemInfo[] itemList = new ItemInfo[]{};

        if (searchString.matches("\\d+:\\d+")) {
            // Match on integer:short to get typeId and subTypeId

            // Retrieve/parse data
            String[] params = searchString.split(":");
            int typeId = Integer.parseInt(params[0]);
            short subTypeId = Short.parseShort(params[1]);

            // Iterate through Items
            for (ItemInfo item : items) {
                // Test for match
                if (item.getId() == typeId && item.getSubTypeId() == subTypeId) {
                    itemList[0] = item;
                    break;
                }
            }
        } else if (searchString.matches("\\d+")) {

            // Retrieve/parse data
            int typeId = Integer.parseInt(searchString);

            // Iterate through Items
            int i = 0;
            for (ItemInfo item : items) {
                // Test for match
                if (item.getId() == typeId) {
                    itemList[i] = item;
                    i++;
                }
            }
        } else {
            // Else this must be a string that we need to identify

            // Iterate through Items
            int i = 0;
            for (ItemInfo item : items) {
                // Look through each possible match criteria
                for (String[] attributes : item.search) {
                    boolean match = false;
                    // Loop through entire criteria strings
                    for (String attribute : attributes) {
                        if (searchString.toLowerCase().contains(attribute)) {
                            match = true;
                            break;
                        }
                    }
                    // THIS was a match
                    if (match) {
                        itemList[i] = item;
                        i++;
                    }
                }
            }
        }

        return itemList;
    }

    /**
     * Single item search function, for when we only ever want to return 1 result
     *
     * @param searchString
     * @return
     */
    public static ItemInfo itemByName(String searchString) {
        ItemInfo matchedItem = null;
        int matchedItemStrength = 0;

        if (searchString.matches("\\d+:\\d+")) {
            // Match on integer:short to get typeId and subTypeId

            // Retrieve/parse data
            String[] params = searchString.split(":");
            int typeId = Integer.parseInt(params[0]);
            short subTypeId = Short.parseShort(params[1]);

            // Iterate through Items
            for (ItemInfo item : items) {
                // Test for match
                if (item.getId() == typeId && item.getSubTypeId() == subTypeId) {
                    matchedItem = item;
                    break;
                }
            }
        } else if (searchString.matches("\\d+")) {
            // Match an integer only, assume subTypeId = 0

            // Retrieve/parse data
            int typeId = Integer.parseInt(searchString);
            short subTypeId = 0;

            // Iterate through Items
            for (ItemInfo item : items) {
                // Test for match
                if (item.getId() == typeId && item.getSubTypeId() == subTypeId) {
                    matchedItem = item;
                    break;
                }
            }
        } else {
            // Else this must be a string that we need to identify

            // Iterate through Items
            for (ItemInfo item : items) {
                // Look through each possible match criteria
                for (String[] attributes : item.search) {
                    boolean match = false;
                    // Loop through entire criteria strings
                    for (String attribute : attributes) {
                        if (searchString.toLowerCase().contains(attribute)) {
                            match = true;
                        } else {
                            match = false;
                            break;
                        }
                    }

                    // THIS was a match
                    if (match) {
                        if (matchedItem == null || attributes.length > matchedItemStrength) {
                            matchedItem = item;
                            matchedItemStrength = attributes.length;
                        }

                        // This criteria was a match, lets break out of this item...no point testing alternate criteria's
                        break;
                    }
                }
            }
        }

        return matchedItem;
    }
    
    /**
     * Joins elements of a String array with the glue between them into a String.
     * @param array
     * @param glue
     * @return
     */
    public static String join(String[] array, String glue) {
        String joined = null;
        for (String element : array) {
            if (joined == null) {
                joined = element;
            } else {
                joined += glue + element;
            }
        }

        if (joined == null) {
            return "";
        } else {
            return joined;
        }
    }
    
    /**
     * Joins elements of a String array with the glue between them into a String.
     * @param list
     * @param glue
     * @return
     */
    public static String join(List<String> list, String glue) {
        String joined = null;
        for (String element : list) {
            if (joined == null) {
                joined = element;
            } else {
                joined += glue + element;
            }
        }

        if (joined == null) {
            return "";
        } else {
            return joined;
        }
    }
}
