package ru.vichukano.trainer.bot.fsm.state;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vichukano.telegram.bot.internal.api.handler.BotMessageHandler;
import ru.vichukano.trainer.bot.telegram.handler.*;
import ru.vichukano.trainer.bot.telegram.keyboard.KeyboardFactory;

@AllArgsConstructor
public enum State {
    START {
        @Override
        public BotMessageHandler<Update, SendMessage> getHandler() {
            return new CreateUserHandler(this);
        }

        @Override
        public SendMessage answer() {
            var answer = new SendMessage();
            answer.setReplyMarkup(new KeyboardFactory().selectSexKeyboard());
            answer.setText("Выберите пол");
            return answer;
        }

        @Override
        public State next() {
            return CREATE_SEX;
        }
    },
    CREATE_SEX {
        @Override
        public BotMessageHandler<Update, SendMessage> getHandler() {
            return new SelectSexHandler(this);
        }

        @Override
        public SendMessage answer() {
            var answer = new SendMessage();
            answer.setReplyMarkup(new KeyboardFactory().startTrainingKeyboard());
            answer.setText("Приступайте к тренировке");
            return answer;
        }

        @Override
        public State next() {
            return START_TRAINING;
        }
    },
    START_TRAINING {
        @Override
        public BotMessageHandler<Update, SendMessage> getHandler() {
            return new StartTrainingHandler(this);
        }

        @Override
        public SendMessage answer() {
            var answer = new SendMessage();
            answer.setText("Выберите упражение или закончите тренировку");
            answer.setReplyMarkup(new KeyboardFactory().selectExerciseKeyboard());
            return answer;
        }

        @Override
        public State next() {
            return SELECT_EXERCISE;
        }
    },
    SELECT_EXERCISE {
        @Override
        public BotMessageHandler<Update, SendMessage> getHandler() {
            return new SelectExerciseHandler(this);
        }

        @Override
        public SendMessage answer() {
            return null;
        }

        @Override
        public State next() {
            return SELECT_WEIGHT;
        }
    },
    SELECT_WEIGHT {
        @Override
        public BotMessageHandler<Update, SendMessage> getHandler() {
            return new SelectWeightHandler(this);
        }

        @Override
        public SendMessage answer() {
            return null;
        }

        @Override
        public State next() {
            return SELECT_REPS;
        }
    },
    SELECT_REPS {
        @Override
        public BotMessageHandler<Update, SendMessage> getHandler() {
            return new SelectRepsHandler(this);
        }

        @Override
        public SendMessage answer() {
            return null;
        }

        @Override
        public State next() {
            return SELECT_EXERCISE;
        }
    },
    FINISH_TRAINING {
        @Override
        public BotMessageHandler<Update, SendMessage> getHandler() {
            return new FinishTrainingHandler();
        }

        @Override
        public SendMessage answer() {
            return null;
        }

        @Override
        public State next() {
            return START_TRAINING;
        }
    };

    public abstract BotMessageHandler<Update, SendMessage> getHandler();

    public abstract SendMessage answer();

    public abstract State next();
}
