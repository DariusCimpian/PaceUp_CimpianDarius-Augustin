using Google.Cloud.Firestore;
using PaceUpServer.Services;

var builder = WebApplication.CreateBuilder(args);

// 1. Legăm fișierul JSON de autentificare
Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", "firebase-key.json");

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// 2. Înregistrăm baza de date Firestore cu ID-ul tău
string projectId = "paceup-c3ac3"; 
builder.Services.AddSingleton(FirestoreDb.Create(projectId));

// 3. Înregistrăm serviciul creat de noi
builder.Services.AddScoped<FirebaseService>();

var app = builder.Build();

// Configurăm Swagger pentru a putea testa API-ul ușor
if (app.Environment.IsDevelopment() || app.Environment.IsProduction())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();
app.MapControllers();

// Pornim pe portul 5000 pentru a fi accesibil
app.Run("http://0.0.0.0:5000");
