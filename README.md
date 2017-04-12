# FBMessagesPuller
Pulls all the messages a Facebook data archive will have between two specified people.

# License
There is no license for my code. Use/Modify as you please. However, this uses the JSoup binaries, which does have it's own
license. Please refer to their license when using their library.

# Usage
You will need to customize the class yourself to be able to use it, but the usage is something like this:

```java
ArchiveLoader loader;
loader = new ArchiveLoader(new File("PathToArchive + File.Separator + "messages.html");

List<Message> messages;
messages = loader.loadMessagesToFrom(new String[] { "Someone" , "Someone2" }, new String[] { "From1" , "From2"});
```

Then handle the messages list as you please. The arrays should only contain the different name variations of the conversation holders considering FB stores your different FB names, if you have ever changed them. Example: Steven T and Steven F may show up as separate names in your messages.

