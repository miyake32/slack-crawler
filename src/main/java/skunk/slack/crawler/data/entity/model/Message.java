package skunk.slack.crawler.data.entity.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import skunk.slack.crawler.util.TimeStampUtils;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Message implements Serializable, Comparable<Message> {
	private static final long serialVersionUID = 1154926259092814187L;

	public Message() {
	}
	
	@Id
	private String messageType;

	@Id
	private String ts;

	@ManyToOne(cascade = {CascadeType.ALL})
	private Channel channel;
	
	@ManyToOne(cascade = {CascadeType.ALL})
	private User user;

	@Column(nullable = false)
	@Lob
	private String text;

	private Timestamp timeStamp;
	
	@OneToMany(cascade = {CascadeType.ALL})
	private List<Reply> replies;

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
		if (messageType == null) {
			if (other.messageType != null)
				return false;
		} else if (!messageType.equals(other.messageType))
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
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((ts == null) ? 0 : ts.hashCode());
		return result;
	}
}
