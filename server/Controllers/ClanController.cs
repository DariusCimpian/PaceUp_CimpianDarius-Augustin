using Microsoft.AspNetCore.Mvc;
using PaceUpServer.Models;
using PaceUpServer.Services;

namespace PaceUpServer.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ClanController : ControllerBase
    {
        private readonly FirebaseService _firebaseService;

        public ClanController(FirebaseService firebaseService)
        {
            _firebaseService = firebaseService;
        }

        [HttpGet]
        public async Task<IActionResult> GetClans()
        {
            var clans = await _firebaseService.GetClansAsync();
            return Ok(clans);
        }

        [HttpGet("{clanId}")]
        public async Task<IActionResult> GetClan(string clanId)
        {
            var clan = await _firebaseService.GetClanAsync(clanId);
            if (clan == null) return NotFound();
            return Ok(clan);
        }

        [HttpGet("leaderboard")]
        public async Task<IActionResult> GetClanLeaderboard()
        {
            var clans = await _firebaseService.GetTopClansAsync();
            return Ok(clans);
        }
    }
}
