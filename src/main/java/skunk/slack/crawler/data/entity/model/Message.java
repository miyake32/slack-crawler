package skunk.slack.crawler.data.entity.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import skunk.slack.crawler.util.TimeStampUtils;

@Entity
@Data
// use deprecated annotation for the compatibility with development env.
@lombok.experimental.Builder
@AllArgsConstructor
public class Message implements Serializable, Comparable<Message> {
	private static final long serialVersionUID = 1154926259092814187L;

	public Message() {
	}

	@Id
	@Column(name = "message_type")
	private String type;

	@Id
	@Column(name = "ts")
	private String ts;

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private Channel channel;

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private User user;

	@Column(nullable = false)
	@Lob
	private String text;

	@Column
	private Timestamp timeStamp;

	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<User> referencedUsers;

	public void setChannel(Channel channel) {
		this.channel = channel;
		channel.addMessage(this);
	}

	public void setTs(String ts) {
		this.ts = ts;
		this.timeStamp = TimeStampUtils.convertSlackTsToTimeStamp(ts);
	}

	@SuppressWarnings("unused")
	private void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public int compareTo(Message o) {
		return this.ts.compareTo(o.ts);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (ts == null) {
			if (other.ts != null)
				return false;
		} else if (!ts.equals(other.ts))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((ts == null) ? 0 : ts.hashCode());
		return result;
	}
}
