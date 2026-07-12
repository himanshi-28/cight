document.querySelectorAll("[data-quiz]").forEach((quiz) => {
  const feedback = quiz.querySelector("[data-feedback]");
  const buttons = quiz.querySelectorAll("button[data-answer]");

  buttons.forEach((button) => {
    button.addEventListener("click", () => {
      const isCorrect = button.dataset.correct === "true";

      feedback.textContent = isCorrect
        ? button.dataset.success
        : button.dataset.retry;
      feedback.className = `feedback ${isCorrect ? "correct" : "incorrect"}`;

      if (isCorrect) {
        buttons.forEach((candidate) => {
          candidate.disabled = true;
        });
      }
    });
  });
});
