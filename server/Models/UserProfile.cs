namespace PaceUpServer.Models
{
    public class UserProfile
    {
        public string Uid { get; set; } = "";
        public string Username { get; set; } = "";
        public string Email { get; set; } = "";
        public int Xp { get; set; } = 0;
        public int Level { get; set; } = 1;
        public double TotalKm { get; set; } = 0.0;
        public string ClanId { get; set; } = "";
        public string ClanRole { get; set; } = "";
    }
}
