import {
  formatRelative,
  formatDistanceToNowStrict,
  intervalToDuration,
} from "date-fns";
import { enGB } from "date-fns/locale";

const format = (sendDate) => {
  const { years, months, days, hours, minutes, seconds } = intervalToDuration({
    start: new Date(sendDate),
    end: new Date(),
  });
  if (!years && !months && !days) {
    const fullSecs = minutes * 60;
    if (!hours && fullSecs < 90) {
      if (fullSecs < 10) {
        return "just now";
      } else if (fullSecs < 90) {
        return "a minute ago";
      }
    } else {
      return formatDistanceToNowStrict(new Date(sendDate), { addSuffix: true });
    }
  } else {
    return formatRelative(new Date(sendDate), new Date(), {
      locale: enGB,
      weekStartsOn: 1,
    }).replace(/\//g, ".");
  }
};

const setSendDate = (message) => ({
  ...message,
  sendDate: new Date(message.sendDate),
  sendDateFormatted: format(message.sendDate),
});

export default setSendDate;
