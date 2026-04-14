using Microsoft.AspNetCore.Mvc;
using PaceUpServer.Models;
using PaceUpServer.Services;

namespace PaceUpServer.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UserController : ControllerBase
    {
        private readonly FirebaseService _firebaseService;

        public UserController(FirebaseService firebaseService)
        {
            _firebaseService = firebaseService;
        }

        [HttpGet("{uid}")]
        public async Task<IActionResult> GetUser(string uid)
        {
            var user = await _firebaseService.GetUserAsync(uid);
            if (user == null) return NotFound();
            return Ok(user);
        }

        [HttpGet("leaderboard")]
        public async Task<IActionResult> GetLeaderboard()
        {
            var users = await _firebaseService.GetTopUsersAsync();
            return Ok(users);
        }
        [HttpGet("status")]
        public IActionResult GetStatus()
        {
         return Ok(new { status = "Online", serverTime=DateTime.Now , database="Connected"});
        }
    }
}
