package skunk.slack.crawler.data.entity.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import skunk.slack.crawler.data.entity.type.ChannelType;

@Entity
@Data
// use deprecated annotation for the compatibility with development env. 
@lombok.experimental.Builder
@AllArgsConstructor
public class Channel implements Serializable {
	private static final long serialVersionUID = -2313544115011327045L;

	public Channel() {
	}
	
	@Id
	private String id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Boolean isMember;

	@Column(nullable = false)
	private ChannelType type;

	@Column(nullable = false)
	private String lastFetchedTs;

	@Transient
	private Set<Message> messages;

	public void addMessage(Message message) {
		if (Objects.isNull(messages)) {
			messages = new HashSet<>();
		}
		messages.add(message);
		if (message.getChannel() != this) {
			message.setChannel(this);
		}
		if (Objects.isNull(this.lastFetchedTs) || message.getTs().compareTo(this.lastFetchedTs) > 0) {
			this.lastFetchedTs = message.getTs();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Channel other = (Channel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

}
