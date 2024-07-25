
# PlayerChangeArmorEvent
Adds the PlayerChangeArmorEvent from paper into spigot/bukkit servers. **Keep in mind** that due to the way packets are handled with servers, canceling this event for players in creative mode can have strange buggy effects.

[![](https://jitpack.io/v/JewishBanana/PlayerChangeArmorEvent.svg)](https://jitpack.io/#JewishBanana/PlayerChangeArmorEvent)

## How To Add

### Maven
To use this simply add into your pom.xml file the jitpack repo like this:
```java
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
```
Next add this repo as a dependency to your pom.xml:
```java
<dependencies>
	<dependency>
		<groupId>com.github.jewishbanana</groupId>
		<artifactId>PlayerChangeArmorEvent</artifactId>
		<version>1.0.3</version>
		<scope>compile</scope>
	</dependency>
</dependencies>
```
Now it should be compiled with your project! The last thing you need to do is hook into the PlayerArmorListener class within your on enable method like this:
```java
public void onEnable() {
    // Your code
    new PlayerArmorListener(this);
}
```
Armor events will now be triggered to the listeners. Check out the How To Use section to see the listener added and an example on usage.

### Not Maven
If you are not using maven then worry not, you can simply just copy and paste the PlayerArmorListener.class and the PlayerArmorChangeEvent.class into your project somewhere and remember to hook into your on enable method the PlayerArmorListener like so:
```java
public void onEnable() {
    // Your code
    new PlayerArmorListener(this);
}
```

## Usage

To use the new event you can just create a listener and hook into the event like so:
```java
@EventHandler
public void onArmorChange(PlayerArmorChangeEvent event) {
    if (event.getNewItem().getType() == Material.DIAMOND_BOOTS)
        event.setCancelled(true);
}
```
This example will prevent any player from equipping diamond boots.
