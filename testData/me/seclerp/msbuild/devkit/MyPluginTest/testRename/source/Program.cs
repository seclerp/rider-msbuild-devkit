namespace MyPluginTest;

class Program
{
  public static void Main(string[] args)
  {
    var spaceshipName = "Millennium Falcon";
    if (spaceship<caret>Name == args[0])
    {
      Console.WriteLine($"{spaceshipName} is authorized");
    }
    else
    {
      Console.WriteLine($"{spaceshipName} is not authorized");
    }
  }
}