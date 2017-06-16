package skunk.slack.crawler.data.entity.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import skunk.slack.crawler.util.TimeStampUtils;

@Entity
@Data
@Builder
@AllArgsConstructor
public class Reply implements Serializable, Comparable<Reply> {
	private static final long serialVersionUID = 3292105374614204876L;

	public Reply() {
	}
	
	@Id
	private String messageType;

	@Id
	private String ts;

	@ManyToOne(cascade = CascadeType.ALL)
	private User user;

	@Column(nullable = false)
	@Lob
	private String text;

	@Column(nullable = false)
	private Timestamp timeStamp;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumns(value = { @JoinColumn(name = "messageType", insertable = false, updatable = false),
			@JoinColumn(name = "ts", insertable = false, updatable = false) })
	private Message message;

	public void setTs(String ts) {
		this.ts = ts;
		this.timeStamp = TimeStampUtils.convertSlackTsToTimeStamp(ts);
	}

	@SuppressWarnings("unused")
	private void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}


	@Override
	public int compareTo(Reply o) {
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
		Reply other = (Reply) obj;
		if (ts == null) {
			if (other.ts != null)
				return false;
		} else if (!ts.equals(other.ts))
			return false;
		if (messageType == null) {
			if (other.messageType != null)
				return false;
		} else if (!messageType.equals(other.messageType))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ts == null) ? 0 : ts.hashCode());
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		return result;
	}
}
