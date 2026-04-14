namespace PaceUpServer.Models
{
    public class Clan
    {
        public string Id { get; set; } = "";
        public string Name { get; set; } = "";
        public string Description { get; set; } = "";
        public int TotalXp { get; set; } = 0;
        public int MemberCount { get; set; } = 0;
        public int MaxMembers { get; set; } = 20;
        public string LeaderId { get; set; } = "";
    }
}
