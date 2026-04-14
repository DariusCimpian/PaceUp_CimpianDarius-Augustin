using Google.Cloud.Firestore;
using PaceUpServer.Models;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace PaceUpServer.Services
{
    public class FirebaseService
    {
        private readonly FirestoreDb _db;

        public FirebaseService(FirestoreDb db)
        {
            _db = db;
        }

        public async Task<UserProfile?> GetUserAsync(string uid)
        {
            var doc = await _db.Collection("users").Document(uid).GetSnapshotAsync();
            if (!doc.Exists) return null;
            return new UserProfile
            {
                Uid = doc.GetValue<string>("uid") ?? "",
                Username = doc.GetValue<string>("username") ?? "",
                Email = doc.GetValue<string>("email") ?? "",
                Xp = (int)(doc.GetValue<long>("xp")),
                Level = (int)(doc.GetValue<long>("level")),
                TotalKm = doc.GetValue<double>("totalKm"),
                ClanId = doc.GetValue<string>("clanId") ?? "",
                ClanRole = doc.GetValue<string>("clanRole") ?? ""
            };
        }

        public async Task<List<UserProfile>> GetTopUsersAsync()
        {
            var snapshot = await _db.Collection("users")
                .OrderByDescending("xp")
                .Limit(20)
                .GetSnapshotAsync();
            return snapshot.Documents.Select(doc => new UserProfile
            {
                Uid = doc.GetValue<string>("uid") ?? "",
                Username = doc.GetValue<string>("username") ?? "",
                Xp = (int)(doc.GetValue<long>("xp")),
                Level = (int)(doc.GetValue<long>("level")),
                TotalKm = doc.GetValue<double>("totalKm")
            }).ToList();
        }

        public async Task<Clan?> GetClanAsync(string clanId)
        {
            var doc = await _db.Collection("clans").Document(clanId).GetSnapshotAsync();
            if (!doc.Exists) return null;
            return new Clan
            {
                Id = doc.GetValue<string>("id") ?? "",
                Name = doc.GetValue<string>("name") ?? "",
                Description = doc.GetValue<string>("description") ?? "",
                TotalXp = (int)(doc.GetValue<long>("totalXp")),
                MemberCount = (int)(doc.GetValue<long>("memberCount")),
                MaxMembers = (int)(doc.GetValue<long>("maxMembers")),
                LeaderId = doc.GetValue<string>("leaderId") ?? ""
            };
        }

        public async Task<List<Clan>> GetClansAsync()
        {
            var snapshot = await _db.Collection("clans")
                .OrderByDescending("totalXp")
                .GetSnapshotAsync();
            return snapshot.Documents.Select(doc => new Clan
            {
                Id = doc.GetValue<string>("id") ?? "",
                Name = doc.GetValue<string>("name") ?? "",
                Description = doc.GetValue<string>("description") ?? "",
                TotalXp = (int)(doc.GetValue<long>("totalXp")),
                MemberCount = (int)(doc.GetValue<long>("memberCount")),
                MaxMembers = (int)(doc.GetValue<long>("maxMembers")),
                LeaderId = doc.GetValue<string>("leaderId") ?? ""
            }).ToList();
        }

        public async Task<List<Clan>> GetTopClansAsync()
        {
            return await GetClansAsync();
        }
    }
}

